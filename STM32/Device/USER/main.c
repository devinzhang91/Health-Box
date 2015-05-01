#include "stm32f10x.h"
#include "usart.h"
#include "timer.h"
#include "delay.h"
#include "adc.h"
#include "dma.h"
#include "usb_lib.h"
#include "hw_config.h"
#include "usb_pwr.h" 

u8 SendBuff[5];
vu16 AD_Value[4];
u8 USB_Data[64]={1};

//设置USB 连接/断线
//enable:0,断开
//       1,允许连接	   
void usb_port_set(u8 enable)
{
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);	  	 
	if(enable)_SetCNTR(_GetCNTR()&(~(1<<1)));//退出断电模式
	else
	{	  
		_SetCNTR(_GetCNTR()|(1<<1));  // 断电模式
		GPIOA->CRH&=0XFFF00FFF;
		GPIOA->CRH|=0X00033000;
		PAout(12)=0;	    		  
	}
}

int main(void)
{
	SendBuff[4]='P';	//	报文头的标识
	
	NVIC_Configuration();				// 设置中断优先级分组
	delay_init();
	
	//USB配置
	usb_port_set(0); 	//USB先断开
	delay_ms(300);
  usb_port_set(1);	//USB再次连接
 	USB_Interrupts_Config();    
 	Set_USBClock();   
 	USB_Init();
	
// 	uart_init(9600);	 							//串口初始化为9600
// 	Adc_Multi_Init();			//多通道ADC初始化
// 	DMA_USART_Config(DMA1_Channel4,(u32)&USART1->DR,(u32)SendBuff,5);
// 	DMA_ADC_Config(DMA1_Channel1,(u32)&ADC1->DR,(u32)AD_Value,4);
// 	
// 	DMA_ADC_Enable(DMA1_Channel1);
// 	TIM4_Int_Init(9999,71);	//定时器设置 72分频 1MHz 定时10000单位 = 10ms
	
	while(1)
	{
		//delay_ms(500);
		USB_SendString("Connect to stm32 test the max lenght and more over 22 Byte. This is DevinZhang USB speed testing, It maybe over 64 Byte");
	}	
		
}
