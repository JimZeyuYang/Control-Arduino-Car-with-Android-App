#include <SPI.h>
#include <WiFi101.h>
#include<Servo.h>
#include <Arduino.h>
#include "wiring_private.h"

// DIGITAL PINS
//  0 --> Serial RX
//  1 --> Serial TX
//  2 --> R DIR
//  3 --> Serial2 RX
//  4 --> Serial2 TX
//  5 --> Wi-Fi Shield
//  6 --> L PWM
//  7 --> Wi-Fi Shield
//  8 --> Neck Servo Motor
//  9 --> R PWM
//  10 --> Wi-Fi Shield
//  11 --> Magnetic SOUTH
//  12 --> Magnetic NORTH
//  13 --> L DIR

// ANALOG PINS
//  A0 --> InfraRed
//  A1 --> Acoustic

Uart Serial2 (&sercom2, 3, 4, SERCOM_RX_PAD_1, UART_TX_PAD_0);

void SERCOM2_Handler() {  Serial2.IrqHandler(); }

Servo neck;
int neckPosn =60;

String outLizard = "";

String radio = "";
int IR = 0;
int acoustic = 0;
boolean south = true;
boolean north = true;

char ssid[] = "JIMMMMMMMMM";
char pass[] = "12345678";
int keyIndex = 0;

int status = WL_IDLE_STATUS;
WiFiServer server(80);

void setup() {
  pinMode(2, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(13, OUTPUT);
  pinMode(11, INPUT_PULLUP);
  pinMode(12, INPUT_PULLUP);

  neck.attach(8);

  if (WiFi.status() == WL_NO_SHIELD) {
    while (true);
  }

  WiFi.config(IPAddress(192, 168, 0, 6));
  status = WiFi.beginAP(ssid,pass);
  
  if (status != WL_AP_LISTENING) {
    while (true);
  }
  
  delay(10000);
  
  server.begin();
  
//  Serial.begin(9600);
//
//    while (!Serial) {
//    ; // wait for serial port to connect. Needed for native USB port only
//  }

}

void loop() {

  if (status != WiFi.status()) {
    status = WiFi.status();
    if (status == WL_AP_CONNECTED) {
      byte remoteMac[6];
      WiFi.APClientMacAddress(remoteMac);
    } else {
    }
  }
  
  WiFiClient client = server.available();

  if (client) {
    String currentLine = "";
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        if (c == '\n') {
          if (currentLine.length() == 0) {
            client.println("HTTP/0.6 200 OK");
            client.println("Content-type:text/html");
            client.println();
            client.print("<title>" + outLizard + "</title>");
            client.println();
            break;
          }
          else {
            currentLine = "";
          }
        } else if (c != '\r') {
          currentLine += c;
        }
        
        if (currentLine.endsWith("GET /6")) {    //LEFT
          drive(200,HIGH,200,LOW);
        } else if (currentLine.endsWith("GET /4")) {    //RIGHT
          drive(200,LOW,200,HIGH);
        } else if (currentLine.endsWith("GET /2")) {    //FORWARD
          drive(200,HIGH,200,HIGH);
        } else if (currentLine.endsWith("GET /8")) {    //BACKWARD
          drive(200,LOW,200,LOW);
        } else if (currentLine.endsWith("GET /1")) {    //LEFT-FORWARD
          drive(75,HIGH,225,HIGH);
        } else if (currentLine.endsWith("GET /7")) {    //LEFT-BACKWARD
          drive(75,LOW,225,LOW);
        } else if (currentLine.endsWith("GET /3")) {    //RIGHT-FORWARD
          drive(225,HIGH,75,HIGH);
        } else if (currentLine.endsWith("GET /9")) {    //RIGHT-BACKWARD
          drive(225,LOW,75,LOW);
        } else if (currentLine.endsWith("GET /5")) {    //STOP
          drive(0,LOW,0,LOW);
        }

        if (currentLine.endsWith("GET /U") && neckPosn < 180) {     //NECK UP
          neckPosn += 15;
        } else if (currentLine.endsWith("GET /D") && neckPosn > 45) {     //NECK DOWN
          neckPosn -= 15;
        } else if (currentLine.endsWith("GET /X")) {      //NECK NO MOVE

        }


        if (currentLine.endsWith("GET /G")) {     //GET DATA
          char data;
          radio = "";
          Serial2.begin(600);
          delay(200);
          pinPeripheral(3, PIO_SERCOM_ALT);
          pinPeripheral(4, PIO_SERCOM_ALT);
          delay(200);
          for (int i=0; i<7; i++) {
            if (Serial2.available()) {
              data = Serial2.read();
              radio += data;
            }
            delay(100);
          }
          delay(100);
          Serial2.end();
          delay(100);
          IR = analogRead(A0);
          acoustic = analogRead(A1);

          if (radio.indexOf("#GAB") >= 0 && acoustic >= 300 && IR <= 200) {
            outLizard = "Gaborus";
          } else if (radio.indexOf("#NUC") >= 0 && acoustic <= 300 && IR <= 200) {
            outLizard = "Nucinkius";
          } else if (radio.indexOf("#DUR") >= 0 && acoustic <= 300 && IR <= 200) {
            outLizard = "Durranis";
          } else if (radio.indexOf("#PER") >= 0 && acoustic <= 300 && IR <= 200) {
            outLizard = "Pereai";
          } else if (IR >= 200 && IR <= 400 && acoustic <= 300) {
            outLizard = "Cheungus";
          } else if (IR >= 400 && acoustic >= 300) {
            outLizard = "Yeatmana";
          } else {
            outLizard = "Lizard cannot be identified";
          }

//          Serial.println(outLizard);
//          Serial.println(radio);
//          Serial.println(IR);
//          Serial.println(acoustic);
//          Serial.println("");

          radio = "";
  
        }

      }
    }
    client.stop();
  }
  neck.write(neckPosn);
}

void drive (int pwmL, int dirL, int pwmR, int dirR) {
  digitalWrite(2, dirR);
  analogWrite(9, pwmR);
  digitalWrite(13, dirL);
  analogWrite(6, pwmL);
}
