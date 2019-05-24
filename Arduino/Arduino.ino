
#define INPUT_SIZE 30
#define OFFT 0
#define HIGHT 255
#define LOWT 20
#define MIDDLE 80 
// Get next command from Serial (add 1 for final 0)
char input[INPUT_SIZE + 1];
unsigned int leds[5] = {3,5,6,9,10};
unsigned int sec = 0,min = 0,hour = 0,day = 0;
unsigned long pastMillis = 0, oneHour = 1000; //oneHour=2083
unsigned int priority[5] = {0,0,0,0,0}; //0=parents ,1=living, 2=kitchen, 3=bathroom, 4=attic

void livingRoomDemo()
{
  if (priority[1] == 0)
  {
    if ((day !=5) and (day !=6) and (hour >= 16)) analogWrite(leds[1], HIGHT);
    else analogWrite(leds[1], LOWT);
  }
  else --priority[1];
}

void kitchenDemo()
{
  if (priority[2] == 0)
  {
    if ( (hour == 7) or (hour == 19)) analogWrite(leds[2], HIGHT);
    else analogWrite(leds[2], LOWT);
  }
  else --priority[2];
}

void parentDemo()
{
  if (priority[0] == 0)
  {
    if (hour <= 6) analogWrite(leds[0], HIGHT);
    else analogWrite(leds[0], LOWT);
  }
  else --priority[0];
}

void bathroomDemo()
{
  if(priority[3] == 0)
  {
    if(hour == 21) analogWrite((leds[3]), HIGHT);
    else analogWrite(leds[3], LOWT);
  }
  else --priority[3];
}

void atticDemo()
{
  if(priority[4] == 0)
  {
    analogWrite(leds[4], LOWT);
  }
  else --priority[4];
}

void demo()
{
  livingRoomDemo();
  kitchenDemo();
  parentDemo();
  bathroomDemo();
  atticDemo();
}



void setup()
{
  Serial.begin(9600);
  for (int led =0; led < sizeof(leds); ++led )
  {
    pinMode(leds[led], OUTPUT);
  }

}

void loop ()
{

  


  byte size = Serial.readBytes(input, INPUT_SIZE);
  // Add the final 0 to end the C string
  input[size] = 0;

  // Read each command pair 
  char* command = strtok(input, "&");
  while (command != NULL)
  {
      // Split the command in two values
      char* separator1 = strchr(command, ':');
      if (separator1 != NULL)
      {
          // Actually split the string in 2: replace ':' with 0
          *separator1 = 0;
          int ledIndex = atoi(command);
          ++separator1;
          int intensity = atoi(separator1);
          analogWrite(leds[ledIndex],intensity);
          priority[ledIndex] = 10;
      }
      // Find the next command in input string
      command = strtok(0, "&");
  }
  //demo();

  
}


