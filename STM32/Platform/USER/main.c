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

//����USB ����/����
//enable:0,�Ͽ�
//       1,��������	   
void usb_port_set(u8 enable)
{
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);	  	 
	if(enable)_SetCNTR(_GetCNTR()&(~(1<<1)));//�˳��ϵ�ģʽ
	else
	{	  
		_SetCNTR(_GetCNTR()|(1<<1));  // �ϵ�ģʽ
		GPIOA->CRH&=0XFFF00FFF;
		GPIOA->CRH|=0X00033000;
		PAout(12)=0;	    		  
	}
}

int main(void)
{
	
	NVIC_Configuration();				// �����ж����ȼ�����
	delay_init();
	
	KEY_Init();
	Dac1_Init();
	LCD_Init();
	Touch_Init();
	//Adc_Init();		  		//��ͨ��ADC��ʼ��
	Adc_Multi_Init();			//��ͨ��ADC��ʼ��
	DMA_USART_Config(DMA1_Channel4,(u32)&USART1->DR,(u32)SendBuff,5);
	DMA_ADC_Config(DMA1_Channel1,(u32)&ADC1->DR,(u32)AD_Value,4);
	uart_init(9600);						//���ڳ�ʼ��Ϊ9600
	
	//USB����
	usb_port_set(0); 	//USB�ȶϿ�
	delay_ms(300);
  usb_port_set(1);	//USB�ٴ�����
 	USB_Interrupts_Config();    
 	Set_USBClock();   
 	USB_Init();
  LCD_Clear(White );		 //д�뱳����ɫ
	
  //LCD_printString(5,5, "ADC test" ,Black);
	delay_ms(1000);
	Add_Button();
	DMA_ADC_Enable(DMA1_Channel1);
	TIM4_Int_Init(99,7199);	//��ʱ������ 7200��Ƶ 10kHz ��ʱ1000��λ = 100ms
	TIM3_Int_Init(999,7199);	//��ʱ������ 7200��Ƶ 10kHz ��ʱ1000��λ = 100ms
	
	SendBuff[4]='P';	//	����ͷ�ı�ʶ
	
	
	while(1)
	{
		if(WK_UP==0) while(LCD_Adjustd());
		delay_ms(500);
		USB_SendString("Connect to stm32 test the max lenght and more over 22 Byte");
		
// 		if(DMA_GetFlagStatus(DMA1_FLAG_TC4)!=RESET)//�ȴ�ͨ��4�������
// 		{
// 			DMA_ClearFlag(DMA1_FLAG_TC4);//���ͨ��4������ɱ�־ 
// 		}
// 		else
// 		{
// 			DMA_GetCurrDataCounter(DMA1_Channel4);//�õ���ǰ��ʣ����ٸ�����
// 		}
	}

}



