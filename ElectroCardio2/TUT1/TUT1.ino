#define sensorPin A0
float sensorValue = 0;
float voltageValue = 0;

void setup() {
  Serial.begin(115200);
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
    sensorValue = analogRead(sensorPin);
    voltageValue = sensorValue*5/1023;
    Serial.print('s');
    Serial.print(voltageValue,2);
    delay(2);

    if (Serial.available() > 0) {
      if (Serial.read() == 'Q') return;
    }
  }
}
