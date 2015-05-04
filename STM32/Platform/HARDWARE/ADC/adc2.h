#ifndef __ADC2_H
#define __ADC2_H	
#include "sys.h"

void Adc2_Init(void);
void Adc2_Multi_Init(void);
void Adc2_Multi_Enable(void);
u16  Get_Adc2(u8 ch); 
u16 Get_Adc2_Average(u8 ch,u8 times);
u16 Get_Multi_Adc2(void);
 
#endif 
