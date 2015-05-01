#ifndef __DMA_H
#define	__DMA_H	   
#include "sys.h"
//用于串口发送
void DMA_USART_Config(DMA_Channel_TypeDef* DMA_CHx,u32 cpar,u32 cmar,u16 cndtr);//配置DMA1_CHx
void DMA_USART_Enable(DMA_Channel_TypeDef*DMA_CHx);//使能DMA1_CHx
//用于ADC多通道
void DMA_ADC_Config(DMA_Channel_TypeDef* DMA_CHx,u32 cpar,u32 cmar,u16 cndtr);//配置DMA1_CHx
void DMA_ADC_Enable(DMA_Channel_TypeDef*DMA_CHx);//使能DMA1_CHx

#endif




