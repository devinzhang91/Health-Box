#ifndef __SPI_H__
#define __SPI_H__

#include "stm32f10x.h"

void SPI2_Init(void);
void CSPin_init(void);
u8 SPI2_ReadWriteByte(u8 byte);

#define	 SPI_CLK_PIN	  GPIO_Pin_13
#define  SPI_CLK_PORT	  GPIOB

#define	 SPI_MISO_PIN	  GPIO_Pin_14
#define	 SPI_MISO_PORT	GPIOB

#define	 SPI_MOSI_PIN	  GPIO_Pin_15
#define	 SPI_MOSI_PORT	GPIOB

#define  SPI_CS_PIN		  GPIO_Pin_12
#define  SPI_CS_PORT		GPIOB

#define  TP_INT_PIN	    GPIO_Pin_7
#define  TP_INT_PORT	  GPIOG

#endif

