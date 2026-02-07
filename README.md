Android APP from ESP32-S3/Arduino project



#include <Arduino.h>
#include <NimBLEDevice.h>

/*
 * NimBLE-Arduino v1.4.2
 * esp32 core v2.0.18 - arduino-esp32 1.0.5
 *
 * PŘÍKAZY po Serialu:
 * 0-9  -> přímé módy podle manufacturerDataList indexu (0..9)
 * b    -> BPM režim (zadáš index módu, ON_ms a OFF_ms)
 * s    -> stop (index 0)
 * h    -> pomoc (výpis)
 */

static uint16_t companyId = 0xFFF0;

#define MANUFACTURER_DATA_PREFIX 0x6D, 0xB6, 0x43, 0xCE, 0x97, 0xFE, 0x42, 0x7C

uint8_t manufacturerDataList[][11] = {
    // 0: Stop all channels (OFF)
    {MANUFACTURER_DATA_PREFIX, 0xE5, 0x15, 0x7D},
    // 1: Set all channels to speed 1
    {MANUFACTURER_DATA_PREFIX, 0xE4, 0x9C, 0x6C},
    // 2: Set all channels to speed 2
    {MANUFACTURER_DATA_PREFIX, 0xE7, 0x07, 0x5E},
    // 3: Set all channels to speed 3
    {MANUFACTURER_DATA_PREFIX, 0xE6, 0x8E, 0x4F},
    // 4–9: další módy (z logu)
    {MANUFACTURER_DATA_PREFIX, 0xE1, 0x31, 0x3B},
    {MANUFACTURER_DATA_PREFIX, 0xE0, 0xB8, 0x2A},
    {MANUFACTURER_DATA_PREFIX, 0xE3, 0x23, 0x18},
    {MANUFACTURER_DATA_PREFIX, 0xE2, 0xAA, 0x09},
    {MANUFACTURER_DATA_PREFIX, 0xED, 0x5D, 0xF1},
    {MANUFACTURER_DATA_PREFIX, 0xEC, 0xD4, 0xE0},
    // Stop 1st channel (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xD5, 0x96, 0x4C},
    // Set 1st channel to speed 1 (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xD4, 0x1F, 0x5D},
    // Set 1st channel to speed 2 (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xD7, 0x84, 0x6F},
    // Set 1st channel to speed 3 (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xD6, 0x0D, 0x7E},
    // Stop 2nd channel (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xA5, 0x11, 0x3F},
    // Set 2nd channel to speed 1 (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xA4, 0x98, 0x2E},
    // Set 2nd channel to speed 2 (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xA7, 0x03, 0x1C},
    // Set 2nd channel to speed 3 (only for toys with 2 channels)
    {MANUFACTURER_DATA_PREFIX, 0xA6, 0x8A, 0x0D},
};

const char *deviceName = "MuSE_Advertiser";

NimBLEAdvertising *pAdvertising;

// režimy
bool bpmMode        = false;
uint8_t manualIndex = 1;
uint8_t bpmIndex    = 1;
unsigned long onMs  = 60;
unsigned long offMs = 1500;
unsigned long lastSwitch = 0;
bool pulseOn        = false;

// ON will send N bursts, OFF always 10 bursts
unsigned long lastRepeat = 0;
const unsigned long manualRepeatMs = 1000; // pro jistotu v manuálním módu

void advertiseIndexRaw(uint8_t index, uint8_t burstCount) {
  NimBLEAdvertising *adv = NimBLEDevice::getAdvertising();

  uint8_t *manufacturerData = manufacturerDataList[index];

  // companyId (2B) + 11B payload = 13 bytů
  std::string data((char *)&companyId, 2);
  data += std::string((char *)manufacturerData, 11);

//  Serial.print("Advertising index ");
//  Serial.print(index);
//  Serial.print(", burstCount=");
//  Serial.print(burstCount);
//  Serial.print(", data: ");
//  for (int i = 0; i < 11; i++) {
//    Serial.print(manufacturerData[i], HEX);
//    if (i < 10) Serial.print(", ");
//  }
//  Serial.println();
//  Serial.flush();

  for (uint8_t i = 0; i < burstCount; i++) {
    adv->stop();
    adv->setManufacturerData(data);
    adv->setScanResponse(true);
    adv->setMinPreferred(0x12);
    adv->setMinPreferred(0x02);
    adv->start();
  }
}

// jednodušší helper pro manuální módy (10× při změně, 1× při opakování)
void advertiseIndex(uint8_t index, bool burst) {
  advertiseIndexRaw(index, burst ? 10 : 1);
}

// Přečte řádku a vrátí číslo
long readNumberLine(const char *prompt, long minVal, long maxVal, long defaultVal) {
  Serial.println(prompt);
  
  // Nejdřív vyčisti starý buffer
  while (Serial.available()) {
    Serial.read();
    delay(1);
  }
  
  // Teď čekej na nový vstup
  while (!Serial.available()) {
    delay(10);
  }
  
  String line = Serial.readStringUntil('\n');
  line.trim();
  if (line.length() == 0) {
    Serial.print("Prázdný vstup, default ");
    Serial.println(defaultVal);
    return defaultVal;
  }
  long v = line.toInt();
  if (v < minVal || v > maxVal) {
    Serial.print("Mimo rozsah, použiju default ");
    Serial.println(defaultVal);
    return defaultVal;
  }
  return v;
}

void setup() {
  Serial.begin(115200);
  Serial.println("MuSE BROADCASTER – staré API, ověřená verze");
  Serial.println("Příkazy: 0-9 = módy, b = BPM, s = stop, h = help");

  NimBLEDevice::init(deviceName);
  pAdvertising = NimBLEDevice::getAdvertising();

  // start manuální mód s burstem
  advertiseIndex(manualIndex, true);
  lastRepeat = millis();
}

void loop() {
  if (Serial.available()) {
    char c = Serial.read();
    if (c >= '0' && c <= '9') {
      bpmMode     = false;
      manualIndex = c - '0';
      advertiseIndex(manualIndex, true);  // při změně 10×
      lastRepeat = millis();
      Serial.print("Přepnuto na manuální mód index ");
      Serial.println(manualIndex);
    } else if (c == 'b' || c == 'B') {
      bpmMode = true;
      pulseOn = false;

      bpmIndex = (uint8_t)readNumberLine(
        "BPM: zadej index módu pro ON (0–12):", 0, 12, 4
      );
      onMs = (unsigned long)readNumberLine(
        "BPM: zadej ON_ms (10–1000):", 10, 10000, 60
      );
      offMs = (unsigned long)readNumberLine(
        "BPM: zadej OFF_ms (50–10000):", 10, 60000, 2000
      );

      lastSwitch = millis();
      lastRepeat = millis();
      Serial.print("BPM režim: index=");
      Serial.print(bpmIndex);
      Serial.print(" ON=");
      Serial.print(onMs);
      Serial.print(" ms, OFF=");
      Serial.print(offMs);
      Serial.println(" ms");
    } else if (c == 's' || c == 'S') {
      bpmMode = false;
      advertiseIndex(0, true);  // stop 10×
      lastRepeat = millis();
      Serial.println("STOP všechny kanály (index 0)");
    } else if (c == 'h' || c == 'H') {
      Serial.println("Příkazy:");
      Serial.println(" 0-9  = přímé módy manufacturerDataList[0..9]");
      Serial.println(" b    = BPM mód – index, ON_ms a OFF_ms (ms)");
      Serial.println(" s    = stop (index 0)");
      Serial.println(" h    = nápověda");
    }
  }

  unsigned long now = millis();

  if (bpmMode) {
    unsigned long period = pulseOn ? onMs : offMs;
    if (now - lastSwitch >= period) {
      pulseOn   = !pulseOn;
      lastSwitch = now;

      if (pulseOn) {
        // ON: počet burstů podle délky (hrubý odhad)
        uint8_t onBursts = max<uint8_t>(1, min<uint8_t>(10, onMs / 20)); 
        advertiseIndexRaw(bpmIndex, onBursts);
        Serial.print("BPM: ON paket, bursts=");
        Serial.println(onBursts);
      } else {
        // OFF: vždy 10×
        advertiseIndexRaw(0, 10);
        Serial.println("BPM: OFF paket (10x)");
      }
      lastRepeat = now;
    }
    // žádné periodické opakování BPM stavu – ať je ON opravdu co nejkratší

  } else {
    // manuální režim – pro jistotu periodické „připomenutí“
    if (now - lastRepeat >= manualRepeatMs) {
      advertiseIndex(manualIndex, false); // 1×
      lastRepeat = now;
    }
  }

  delay(5);
}
