#ifndef __DMA_H
#define	__DMA_H	   
#include "sys.h"
//���ڴ��ڷ���
void DMA_USART_Config(DMA_Channel_TypeDef* DMA_CHx,u32 cpar,u32 cmar,u16 cndtr);//����DMA1_CHx
void DMA_USART_Enable(DMA_Channel_TypeDef*DMA_CHx);//ʹ��DMA1_CHx
//����ADC��ͨ��
void DMA_ADC_Config(DMA_Channel_TypeDef* DMA_CHx,u32 cpar,u32 cmar,u16 cndtr);//����DMA1_CHx
void DMA_ADC_Enable(DMA_Channel_TypeDef*DMA_CHx);//ʹ��DMA1_CHx

#endif




