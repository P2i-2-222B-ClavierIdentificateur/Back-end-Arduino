#include  <SPI.h>
#include  <RF24.h>
#include  "packet.h"

RF24 radio(9,10); 
uint8_t address[6] = {0x3C,0xCE,0xCC,0xCE,0xCC};   // Adresse du pipe
payload_t rcv_val;
unsigned long tps;

void setup() {
  Serial.begin(115200);    // Initialiser la communication série 
  //Serial.println (F("Starting my first test")) ;
  
  radio.begin();
  radio.setChannel(0x3c);
  radio.setDataRate(RF24_2MBPS);
  radio.enableDynamicPayloads();

  radio.openReadingPipe(0,address); // Ouvrir le Pipe en lecture
  radio.startListening();
}

void loop(void) {
    unsigned long wait = micros();
    boolean timeout = false;
    
    while (radio.available()) 
      {
      radio.read(&rcv_val, sizeof(payload_t));
      if (rcv_val.pression>0){
      Serial.print(rcv_val.id);
      Serial.print("_");
      Serial.print(rcv_val.pression);
      Serial.print("_");
      Serial.println(rcv_val.cmpt);
      }
      }
}
