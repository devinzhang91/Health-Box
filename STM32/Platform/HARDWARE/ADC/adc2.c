 #include "adc2.h"
 #include "delay.h"  
		   
extern vu32 AD_Value[];
			 
//��ʼ��ADC
//���Թ���ͨ��Ϊ��
//Ĭ�Ͽ���ͨ��2																	   
void  Adc2_Init(void)
{ 	
	ADC_InitTypeDef ADC_InitStructure; 
	GPIO_InitTypeDef GPIO_InitStructure;

	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB |RCC_APB2Periph_ADC2	, ENABLE );	  //ʹ��ADC2ͨ��ʱ��
 

	RCC_ADCCLKConfig(RCC_PCLK2_Div6);   //����ADC��Ƶ����6 72M/6=12,ADC���ʱ�䲻�ܳ���14M

	//PB0 ��Ϊģ��ͨ����������                         
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;		//ģ����������
	GPIO_Init(GPIOB, &GPIO_InitStructure);	

	ADC_DeInit(ADC2);  //��λADC2,������ ADC2 ��ȫ���Ĵ�������Ϊȱʡֵ

	ADC_InitStructure.ADC_Mode = ADC_Mode_Independent;	//ADC����ģʽ:ADC1��ADC2�����ڶ���ģʽ
	ADC_InitStructure.ADC_ScanConvMode = DISABLE;	//ģ��ת�������ڵ�ͨ��ģʽ
	ADC_InitStructure.ADC_ContinuousConvMode = DISABLE;	//ģ��ת�������ڵ���ת��ģʽ
	ADC_InitStructure.ADC_ExternalTrigConv = ADC_ExternalTrigConv_None;	//ת��������������ⲿ��������
	ADC_InitStructure.ADC_DataAlign = ADC_DataAlign_Right;	//ADC�����Ҷ���
	ADC_InitStructure.ADC_NbrOfChannel = 1;	//˳����й���ת����ADCͨ������Ŀ
	ADC_Init(ADC2, &ADC_InitStructure);	//����ADC_InitStruct��ָ���Ĳ�����ʼ������ADCx�ļĴ���   

	ADC_Cmd(ADC2, ENABLE);	//ʹ��ָ����ADC2
	
	ADC_ResetCalibration(ADC2);	//ʹ�ܸ�λУ׼  
	 
	while(ADC_GetResetCalibrationStatus(ADC2));	//�ȴ���λУ׼����
	
	ADC_StartCalibration(ADC2);	 //����ADУ׼
 
	while(ADC_GetCalibrationStatus(ADC2));	 //�ȴ�У׼����
 
	ADC_SoftwareStartConvCmd(ADC2, ENABLE);		//ʹ��ָ����ADC2�����ת����������
	
	//�����������
	ADC_RegularChannelConfig(ADC2, ADC_Channel_8, 1, ADC_SampleTime_239Cycles5 );	//ADC2,ADCͨ��,����ʱ��Ϊ239.5����	  			     
	ADC_SoftwareStartConvCmd(ADC2, ENABLE);		//ʹ��ָ����ADC2�����ת����������

}		

//��ʼ��ADC2
//������ö�ͨ����������������DMA1��ͨ������
//����Ĭ�Ͻ�����ͨ��10~13
//��Ӧ�ܽ�PC0~3
void Adc2_Multi_Init(void)
{ 	
	ADC_InitTypeDef ADC_InitStructure; 
	GPIO_InitTypeDef GPIO_InitStructure;

	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOC |RCC_APB2Periph_ADC2	, ENABLE );	  //ʹ��ADC2ͨ��ʱ��
 

	RCC_ADCCLKConfig(RCC_PCLK2_Div6);   //����ADC��Ƶ����6 72M/6=12,ADC���ʱ�䲻�ܳ���14M

	//PB0,1 ��Ϊģ��ͨ����������
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0|GPIO_Pin_1|GPIO_Pin_2|GPIO_Pin_3;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;		//ģ����������
	GPIO_Init(GPIOC, &GPIO_InitStructure);	

	ADC_DeInit(ADC2);  //��λADC2,������ ADC2 ��ȫ���Ĵ�������Ϊȱʡֵ

	ADC_InitStructure.ADC_Mode = ADC_Mode_RegSimult;	//ADC����ģʽ:ADC1ͬ��������ģʽ
	ADC_InitStructure.ADC_ScanConvMode =ENABLE; //ģ��ת��������ɨ��ģʽ
	ADC_InitStructure.ADC_ContinuousConvMode = ENABLE; //ģ��ת������������ת��ģʽ
	ADC_InitStructure.ADC_ExternalTrigConv = ADC_ExternalTrigConv_None;	//ת��������������ⲿ��������
	ADC_InitStructure.ADC_DataAlign = ADC_DataAlign_Right;	//ADC�����Ҷ���
	ADC_InitStructure.ADC_NbrOfChannel = 4;	//˳����й���ת����ADCͨ������Ŀ
	ADC_Init(ADC2, &ADC_InitStructure);	//����ADC_InitStruct��ָ���Ĳ�����ʼ������ADCx�ļĴ���   

	ADC_RegularChannelConfig(ADC2, ADC_Channel_10, 1, ADC_SampleTime_239Cycles5 );
	ADC_RegularChannelConfig(ADC2, ADC_Channel_11, 2, ADC_SampleTime_239Cycles5 );
	ADC_RegularChannelConfig(ADC2, ADC_Channel_12, 3, ADC_SampleTime_239Cycles5 );
	ADC_RegularChannelConfig(ADC2, ADC_Channel_13, 4, ADC_SampleTime_239Cycles5 );

	ADC_ExternalTrigConvCmd(ADC2, ENABLE);                                        //ʹ��ADC2���ⲿ����ģʽ 

	// ����ADC��DMA֧�֣�Ҫʵ��DMA���ܣ������������DMAͨ���Ȳ�����
	//ADC_DMACmd(ADC2, ENABLE);

	

}		


void Adc2_Multi_Enable(void)
{
	ADC_Cmd(ADC2, ENABLE);	//ʹ��ָ����ADC2
	
	ADC_ResetCalibration(ADC2);	//ʹ�ܸ�λУ׼  
	 
	while(ADC_GetResetCalibrationStatus(ADC2));	//�ȴ���λУ׼����
	
	ADC_StartCalibration(ADC2);	 //����ADУ׼
 
	while(ADC_GetCalibrationStatus(ADC2));	 //�ȴ�У׼����
 
	//ADC_SoftwareStartConvCmd(ADC2, ENABLE);		//ʹ��ָ����ADC2�����ת����������
}
		  
//���λ��ADCֵ
//ch:ͨ��ֵ 0~3
u16 Get_Adc2(u8 ch)   
{
	u16 tempADC = 0; 
	
	while(!ADC_GetFlagStatus(ADC2, ADC_FLAG_EOC ));//�ȴ�ת������
	tempADC = ADC_GetConversionValue(ADC2);
	
	//����ָ��ADC�Ĺ�����ͨ����һ�����У�����ʱ��
	ADC_RegularChannelConfig(ADC2, ch, 1, ADC_SampleTime_239Cycles5 );	//ADC2,ADCͨ��,����ʱ��Ϊ239.5����	  			     
	ADC_SoftwareStartConvCmd(ADC2, ENABLE);		//ʹ��ָ����ADC2�����ת����������	

	return tempADC;	//�������һ��ADC2�������ת�����
}

//���λ��ƽ��ADCֵ
//ch:ͨ��ֵ 0~3 ; times:ȡֵ���� 0~255
u16 Get_Adc2_Average(u8 ch,u8 times)
{
	u32 temp_val=0;
	u8 t;
	for(t=0;t<times;t++)
	{
		temp_val+=Get_Adc2(ch);
		delay_ms(5);
	}
	return temp_val/times;
} 	 

//�����ͨ��ADCֵ
//AD_Value2[]��DMAĿ���ַ������ռ�
u16 Get_Multi_Adc2(void)
{
	u32 temp_val=0;
	u8 t;
	for(t=0;t<4;t++)
	{
		temp_val+=((AD_Value[t]>>16) & 0xffff);
	}
	return temp_val/4;
} 

























