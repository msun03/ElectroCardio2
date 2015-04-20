#include <medianFilter.h>

medianFilter Filter;

#define sensorPin A0
float sensorValue = 0;
float voltageValue = 0;

void setup() {
  Serial.begin(115200);
  Filter.begin();
}

void loop() {
  if (Serial.available() > 0) {
    char re = Serial.read();

    switch (re) {
      case 'E':
        start();
        break;
    }
  }
}

void start() {
  while (1) {
    sensorValue = Filter.run(analogRead(sensorPin));
    voltageValue = sensorValue*5/1023;
    Serial.print('s');
    Serial.print(voltageValue,2);
    delay(2);

    if (Serial.available() > 0) {
      if (Serial.read() == 'Q') return;
    }
  }
}
