#ifndef __ADC_H
#define __ADC_H	
#include "sys.h"

void Adc_Init(void);
void Adc_Multi_Init(void);
u16  Get_Adc(u8 ch); 
u16 Get_Adc_Average(u8 ch,u8 times);
u16 Get_Multi_Adc(void);
 
#endif 
