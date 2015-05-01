#include "stm32f10x.h"
#include "usart.h"
#include "timer.h"
#include "LCD.h"
#include "delay.h"
#include "touch.h"
#include "adc.h"
#include "key.h"
#include "dac.h"
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
	
	NVIC_Configuration();				// 设置中断优先级分组
	delay_init();
	
	KEY_Init();
	Dac1_Init();
	LCD_Init();
	Touch_Init();
	//Adc_Init();		  		//单通道ADC初始化
	Adc_Multi_Init();			//多通道ADC初始化
	DMA_USART_Config(DMA1_Channel4,(u32)&USART1->DR,(u32)SendBuff,5);
	DMA_ADC_Config(DMA1_Channel1,(u32)&ADC1->DR,(u32)AD_Value,4);
	uart_init(9600);						//串口初始化为9600
	
	//USB配置
	usb_port_set(0); 	//USB先断开
	delay_ms(300);
  usb_port_set(1);	//USB再次连接
 	USB_Interrupts_Config();    
 	Set_USBClock();   
 	USB_Init();
  LCD_Clear(White );		 //写入背景颜色
	
  //LCD_printString(5,5, "ADC test" ,Black);
	delay_ms(1000);
	Add_Button();
	DMA_ADC_Enable(DMA1_Channel1);
	TIM4_Int_Init(99,7199);	//定时器设置 7200分频 10kHz 定时1000单位 = 100ms
	TIM3_Int_Init(999,7199);	//定时器设置 7200分频 10kHz 定时1000单位 = 100ms
	
	SendBuff[4]='P';	//	报文头的标识
	
	
	while(1)
	{
		if(WK_UP==0) while(LCD_Adjustd());
		delay_ms(500);
		USB_SendString("Connect to stm32 test the max lenght and more over 22 Byte");
		
// 		if(DMA_GetFlagStatus(DMA1_FLAG_TC4)!=RESET)//等待通道4传输完成
// 		{
// 			DMA_ClearFlag(DMA1_FLAG_TC4);//清除通道4传输完成标志 
// 		}
// 		else
// 		{
// 			DMA_GetCurrDataCounter(DMA1_Channel4);//得到当前还剩余多少个数据
// 		}
	}

}



