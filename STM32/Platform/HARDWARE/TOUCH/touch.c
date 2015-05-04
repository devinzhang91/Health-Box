#include "touch.h" 
#include "delay.h"	 
#include "LCD.h"
#include "spi.h"
#include "math.h"
#include "dac.h"
#include "dma.h"
	 	   
void touch_show(void);

u16 x[5],y[5]={0};	//��㶨λ���߼�����
float KX=-5.4100,KY=7.6900;				//����ϵ��
u16 XLC=1020,YLC=945,XC=160,YC=120;	//���Ļ�׼ϵ��
extern u8 AdcChannel = 1;

u16 ADS_Read(u8 CMD)	  
{ 	 	  
	u16 Num=0,temp;  	 
	TCS_LOW; 
	SPI2_ReadWriteByte(CMD);
	delay_us(6);	 
	temp=SPI2_ReadWriteByte(0x00); 
	Num=temp<<8; 
	delay_us(1); 
	temp=SPI2_ReadWriteByte(0x00); 
	Num|=temp;  	
	Num>>=4;
	TCS_HIGH; 
	return(Num);  
} 

u16 Read_XY(u8 CMD)
{
	u16 i, j;
	u16 buf[10];
	u16 sum=0;
	u16 temp;
	for(i=0;i<10;i++)buf[i]=ADS_Read(CMD);				    
	for(i=0;i<9; i++)
	{
		for(j=i+1;j<10;j++)
		{
			if(buf[i]>buf[j])
			{
				temp=buf[i];
				buf[i]=buf[j];
				buf[j]=temp;
			}
		}
	}	  
	for(i=3;i<7;i++)sum+=buf[i];
	temp=sum/(4);
	return temp;   
}

u16 Read_X(void)
{
	u16 tempx;
	tempx = (Read_XY(CMD_RDX)-XLC)/(KX) + XC;
	return tempx;
}
u16 Read_Y(void)
{
	u16 tempy;
	tempy = (Read_XY(CMD_RDY)-YLC)/(KY) + YC;
	return tempy;
}

void Touch_Init(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	NVIC_InitTypeDef NVIC_InitStructure;
	EXTI_InitTypeDef EXTI_InitStructure;
	
// 	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOG, ENABLE);
// 	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_7;//pen_int:PG7  
// 	GPIO_InitStructure.GPIO_Mode  = GPIO_Mode_IPU;
// 	GPIO_Init(GPIOG, &GPIO_InitStructure);
	
	
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOG | RCC_APB2Periph_AFIO,ENABLE);
	GPIO_InitStructure.GPIO_Pin   = GPIO_Pin_7;
	GPIO_InitStructure.GPIO_Mode  = GPIO_Mode_IPU;
	//GPIO_PinRemapConfig(GPIO_Remap_SWJ_JTAGDisable, ENABLE);//�ر�jtag��ʹ��SWD��������SWDģʽ����
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_2MHz;
	GPIO_Init(GPIOG,&GPIO_InitStructure);
	GPIO_EXTILineConfig(GPIO_PortSourceGPIOG, GPIO_PinSource7);	//���ùܽ�Ϊ�ⲿ�жϹܽ�
	EXTI_InitStructure.EXTI_Line    = EXTI_Line7;
	EXTI_InitStructure.EXTI_Mode    = EXTI_Mode_Interrupt;	//Ϊ�ж�����
	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Falling;//Falling�½��� Rising����
	EXTI_InitStructure.EXTI_LineCmd = ENABLE;
	EXTI_Init(&EXTI_InitStructure);
	EXTI_ClearITPendingBit(EXTI_Line7);	   //�����·����λ
	/* Enable the EXTI4 Interrupt */
	NVIC_InitStructure.NVIC_IRQChannel = EXTI9_5_IRQn  ;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 3;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
	
	SPI2_Init();
	CSPin_init();
}

/****************************************************************************
* ��    �ƣ�void LCD_Adjustd(void)
* ��    �ܣ�У��������ϵ��
* ��ڲ�����	null
* ���ڲ�������
* ˵    ����null
* ���÷�����LCD_Adjustd();
****************************************************************************/
u8 LCD_Adjustd(void)
{
	EXTI_InitTypeDef EXTI_InitStructure;
	
	EXTI_InitStructure.EXTI_Line    = EXTI_Line7;
	EXTI_InitStructure.EXTI_Mode    = EXTI_Mode_Interrupt;	//Ϊ�ж�����
	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Falling;//Falling�½��� Rising����
	EXTI_InitStructure.EXTI_LineCmd = DISABLE;
	EXTI_Init(&EXTI_InitStructure);
	//��ʾֹͣˢ��
	TIM_Cmd(TIM3, DISABLE);  //ʹ��TIMx����
	
	LCD_Clear(White );
	LCD_printString(110,20, "Adjustd Begin" ,Black);
	delay_ms(5000);
	// ����һ����
	LCD_Draw_Target(20, 20, Red);
	while( GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7));
	while( (1-GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7)))
	{
		x[0] = Read_XY(CMD_RDX);
		y[0] = Read_XY(CMD_RDY); 
		LCD_ShowNum(150,80,x[0],Black);
		LCD_ShowNum(150,110,y[0],Black);
		delay_ms(200);
		LCD_Color_Fill(150,80,200,120, White);
	}
	// ���ڶ�����
	LCD_Draw_Target(300, 20, Red);
	LCD_Draw_Target(20, 20, White);
	while( GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7));
	while( (1-GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7)))
	{
		x[1] = Read_XY(CMD_RDX);
		y[1] = Read_XY(CMD_RDY); 
		LCD_ShowNum(150,80,x[1],Black);
		LCD_ShowNum(150,110,y[1],Black);
		delay_ms(200);
		LCD_Color_Fill(150,80,200,120, White);
	}
	if(abs(y[1]-y[0]) >60)
	{
		LCD_Clear(White );
		LCD_printString(110,20, "Adjustd Fail" ,Black);
		delay_ms(5000);
		LCD_Clear(White );
		return 1;
	}
	// ����������
	LCD_Draw_Target(20, 220, Red);
	LCD_Draw_Target(300, 20, White);
	while( GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7));
	while( (1-GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7)))
	{
		x[2] = Read_XY(CMD_RDX);
		y[2] = Read_XY(CMD_RDY); 
		LCD_ShowNum(150,80,x[2],Black);
		LCD_ShowNum(150,110,y[2],Black);
		delay_ms(200);
		LCD_Color_Fill(150,80,200,120, White);
	}
	if(abs(x[2]-x[0]) >80)
	{
		LCD_Clear(White );
		LCD_printString(110,20, "Adjustd Fail" ,Black);
		delay_ms(5000);
		LCD_Clear(White );
		return 1;
	}
	// �����ĸ���
	LCD_Draw_Target(300, 220, Red);
	LCD_Draw_Target(20, 220, White);
	while( GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7));
	while( (1-GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7)))
	{
		x[3] = Read_XY(CMD_RDX);
		y[3] = Read_XY(CMD_RDY); 
		LCD_ShowNum(150,80,x[3],Black);
		LCD_ShowNum(150,110,y[3],Black);
		delay_ms(200);
		LCD_Color_Fill(150,80,200,120, White);
	}
	if((abs(y[2]-y[3]) >60) || (abs(x[1]-x[3]) >80))
	{
		LCD_Clear(White );
		LCD_printString(110,20, "Adjustd Fail" ,Black);
		delay_ms(5000);
		LCD_Clear(White );
		return 1;
	}
	// ���������
	LCD_Draw_Target(160, 120, Red);
	LCD_Draw_Target(300, 220, White);
	while( GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7));
	while( (1-GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7)))
	{
		x[4] = Read_XY(CMD_RDX);
		y[4] = Read_XY(CMD_RDY);
		delay_ms(200);
	}
	//����У��ϵ��
// 	KX  = ((abs(y[0]-y[2])/280+abs(y[1]-y[3])/280)/2);
// 	KY  = ((abs(x[0]-x[1])/200+abs(x[2]-x[3])/200)/2);
	KX  = (((float)(y[0]-y[2])/280+(float)(y[1]-y[3])/280)/2);
	KY  = (((float)(x[0]-x[1])/200+(float)(x[2]-x[3])/200)/2);
	XC = 160;
	YC = 120;
	XLC  = y[4];
	YLC  = x[4];
	
	// �������
	LCD_Clear(White );
	LCD_printString(110,20, "Adjustd Done" ,Black);
	delay_ms(5000);
	LCD_Color_Fill(110,20,200,35, White);
	LCD_printString(110,20, "Testing" ,Black);
	
	EXTI_InitStructure.EXTI_Line    = EXTI_Line7;
	EXTI_InitStructure.EXTI_Mode    = EXTI_Mode_Interrupt;	//Ϊ�ж�����
	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Falling;//Falling�½��� Rising����
	EXTI_InitStructure.EXTI_LineCmd = ENABLE;
	EXTI_Init(&EXTI_InitStructure);
	EXTI_ClearITPendingBit(EXTI_Line7);	   //�����·����λ
	
	//��ʾ��ʼˢ��
	TIM_Cmd(TIM3, ENABLE);  //ʹ��TIMx����
	
	Add_Button();
	
	return 0;
}

u16 dacValue = 1100;

void touch_show(void)
{
	while( (1-GPIO_ReadInputDataBit(GPIOG,GPIO_Pin_7)))
	{
		u16 x,y=0;
		x=Read_X();
		y=Read_Y();
			//������ʾ
// 		LCD_Color_Fill(5,5,25,25, White);
// 		//LCD_Clear(White );
// 		LCD_ShowNum(5,5,x,Black);
// 		LCD_ShowNum(5,15,y,Black);
		
		
		if( 40<x && x<120 && 140<y && y<200 )
		{
			AdcChannel = 1;
			LCD_Color_Fill(80,10,90,20, White);
			LCD_ShowNum(80,10,1,Black);
		}
		
		if( 200<x && x<280 && 140<y && y<200 ) 
		{			
			AdcChannel = 2;
			LCD_Color_Fill(80,10,90,20, White);
			LCD_ShowNum(80,10,2,Black);
		}
	
		delay_ms(100);
	}
}

//��������Ӧ
void EXTI9_5_IRQHandler(void)
{
	touch_show();
	EXTI_ClearITPendingBit(EXTI_Line7);
}





