#ifndef __ADC1_H
#define __ADC1_H	
#include "sys.h"

void Adc1_Init(void);
void Adc1_Multi_Init(void);
void Adc1_Multi_Enable(void);
u16  Get_Adc1(u8 ch); 
u16 Get_Adc1_Average(u8 ch,u8 times);
u16 Get_Multi_Adc1(void);
 
#endif 
