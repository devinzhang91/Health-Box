#ifndef __ADC3_H
#define __ADC3_H	
#include "sys.h"

void Adc3_Init(void);
void Adc3_Multi_Init(void);
u16  Get_Adc3(u8 ch); 
u16 Get_Adc3_Average(u8 ch,u8 times);
u16 Get_Multi_Adc3(void);
 
#endif 
