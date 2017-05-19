#include  <SPI.h>
#include  <RF24.h>
#include  "packet.h"

double sensorValues [6] ;
int cmpt [6] ;
RF24 radio(40,53);
uint8_t address[6] = {0x3C,0xCE,0xCC,0xCE,0xCC};   // Adresse du pipe
payload_t val;  // Valeur à envoyer
unsigned long tps;
int toucheAct = 10;
boolean toucheAppuyeeAct[6];
boolean toucheAppuyeePre[6];

void setup() {
  Serial.begin(115200);    // Initialiser la communication série 
  Serial.println (F("Starting my first test")) ;
  cmpt[0]=cmpt[1]=cmpt[2]=cmpt[3]=cmpt[4]=cmpt[5]=0;
  toucheAppuyeeAct[0]=toucheAppuyeeAct[1]=toucheAppuyeeAct[2]=toucheAppuyeeAct[3]=toucheAppuyeeAct[4]=toucheAppuyeeAct[5] = false;
  toucheAppuyeePre[0]=toucheAppuyeePre[1]=toucheAppuyeePre[2]=toucheAppuyeePre[3]=toucheAppuyeePre[4]=toucheAppuyeePre[5]=false;
  radio.begin();
  radio.setChannel(0x3c);
  radio.setDataRate(RF24_2MBPS);
  radio.setPALevel(RF24_PA_MAX);
  radio.setRetries(4,4);
  radio.enableDynamicPayloads();
  radio.openWritingPipe(address);    // Ouvrir le Pipe en écriture
  radio.stopListening();  
             
}

void loop(void) {
    // read the input on analog pin 0,1,2,3,4,5:
  sensorValues[0] = analogRead(A0); //touche p
  sensorValues[1] = analogRead(A1); //touche a
  sensorValues[2] = analogRead(A2); //touche s
  sensorValues[3] = analogRead(A3); //touche w
  sensorValues[4] = analogRead(A4);//touche o
  sensorValues[5] = analogRead(A5);//touche r
  
  // print out the value you read:
  val.pression=0;
  for (int i = 0 ; i < 3 ; i++){
    char c;
    
    if (sensorValues[i]> 0){
      
      switch(i) {
        case 0 :
         c = 'p'; 
         break;
        case 1 :
         c = 'a';
         break;
        case 2 :
         c = 's';
         break;
        case 3 :
         c = 'w';
         break;
        case 4 :
         c = 'o';
         break;
        case 5 :
         c = 'r';
         break;
      }
         toucheAppuyeePre[i]=toucheAppuyeeAct[i];
         toucheAppuyeeAct[i]=true;
         if(toucheAppuyeePre[i]==false && toucheAppuyeeAct[i]==true){
          cmpt[i]++;
          //toucheAct=i;
         }
          val.pression=sensorValues[i];
          val.cmpt=cmpt[i];
          val.id=c;
     }
     
    else{
      
      toucheAppuyeePre[i]=toucheAppuyeeAct[i];
       toucheAppuyeeAct[i]=false;
     }
    
    
    
    
  }
          Serial.print(F("\n Now sending Packet "));
          Serial.print(tps/1000000);
          Serial.print(F("        "));
          Serial.print(val.pression);
          Serial.print("        ");
          Serial.print(val.cmpt);
          Serial.print("        ");
          Serial.print(val.id);

     
  
  if (radio.write(&val,sizeof(payload_t)) ) 
        { Serial.print(F(" ... Ok ... "));     
        }
        else
        { Serial.print(F(" ... failed ... ")); 
        }

  Serial.println("");
  delay(5);
  }

