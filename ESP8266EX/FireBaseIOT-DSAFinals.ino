#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>


#define WIFI_SSID "WIFISSID"
#define WIFI_PASSWORD "WIFIPASS"


#define API_KEY "APIforFirebase"


#define DATABASE_URL "url here" 

#define USER_EMAIL "@gmail.com"
#define USER_PASSWORD "d"


FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;

const int ledPin = 4;

void setup()
{
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();


  config.api_key = API_KEY;

 
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  
  config.database_url = DATABASE_URL;

  Firebase.reconnectNetwork(true);

 
  fbdo.setBSSLBufferSize(4096 , 1024 );


  fbdo.setResponseSize(2048);

  Firebase.begin(&config, &auth);

  Firebase.setDoubleDigits(5);

  config.timeout.serverResponse = 10 * 1000;
}

void loop()
{

  if (Firebase.ready() && (millis() - sendDataPrevMillis > 1000 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();

  int ledState;
   if(Firebase.RTDB.getInt(&fbdo, "/led/state", &ledState)){
    digitalWrite(ledPin, ledState);
   }else{
    Serial.println(fbdo.errorReason().c_str());
   }
  }
}
