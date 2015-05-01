#include "spi.h"

void SPI2_Init(void)
{
  	GPIO_InitTypeDef GPIO_InitStructure;
  	SPI_InitTypeDef  SPI_InitStructure;
		
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB|RCC_APB2Periph_AFIO, ENABLE );	
		RCC_APB1PeriphClockCmd(RCC_APB1Periph_SPI2,ENABLE);
	
		GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13 | GPIO_Pin_14 | GPIO_Pin_15;
		GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;  //�����������
		GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
		GPIO_Init(GPIOB, &GPIO_InitStructure);

		SPI_Cmd(SPI2, DISABLE); //��ֹSPI����
		SPI_InitStructure.SPI_Direction = SPI_Direction_2Lines_FullDuplex;  //����SPI�������˫�������ģʽ:SPI����Ϊ˫��˫��ȫ˫��
		SPI_InitStructure.SPI_Mode = SPI_Mode_Master;		//����SPI����ģʽ:����Ϊ��SPI
		SPI_InitStructure.SPI_DataSize = SPI_DataSize_8b;		//����SPI�����ݴ�С:SPI���ͽ���8λ֡�ṹ
		SPI_InitStructure.SPI_CPOL = SPI_CPOL_High;		//ѡ���˴���ʱ�ӵ���̬:ʱ�����ո�
		SPI_InitStructure.SPI_CPHA = SPI_CPHA_2Edge;	//���ݲ����ڵ�һ��ʱ����
		SPI_InitStructure.SPI_NSS = SPI_NSS_Soft;		//NSS�ź���Ӳ����NSS�ܽţ����������ʹ��SSIλ������:�ڲ�NSS�ź���SSIλ����
		SPI_InitStructure.SPI_BaudRatePrescaler = SPI_BaudRatePrescaler_64;		//���岨����Ԥ��Ƶ��ֵ:������Ԥ��ƵֵΪ256
		SPI_InitStructure.SPI_FirstBit = SPI_FirstBit_MSB;	//ָ�����ݴ����MSBλ����LSBλ��ʼ:���ݴ����MSBλ��ʼ
		SPI_InitStructure.SPI_CRCPolynomial = 7;	//CRCֵ����Ķ���ʽ
		SPI_Init(SPI2, &SPI_InitStructure);  //����SPI_InitStruct��ָ���Ĳ�����ʼ������SPIx�Ĵ���
	 
		SPI_Cmd(SPI2, ENABLE); //ʹ��SPI����
		
		//SPI2_ReadWriteByte(0xff);//��������
}

void CSPin_init(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;

  	RCC_APB2PeriphClockCmd( RCC_APB2Periph_GPIOB | RCC_APB2Periph_GPIOD, ENABLE);
  	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12;//MMACS:PD12
		GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;  //�����������
		GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  	GPIO_Init(GPIOD, &GPIO_InitStructure);
  	GPIO_SetBits(GPIOD, GPIO_Pin_12);//��ѡ��
	
  	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12;//TPCS:PB12
  	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;  //�����������
		GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  	GPIO_Init(GPIOB, &GPIO_InitStructure);
  	GPIO_SetBits(GPIOB, GPIO_Pin_12);//��ѡ��
	
  	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13;//FLCS:PD13
  	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;  //�����������
		GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  	GPIO_Init(GPIOD, &GPIO_InitStructure);
  	GPIO_SetBits(GPIOD, GPIO_Pin_13);//��ѡ��
}

u8 SPI2_ReadWriteByte(u8 byte)
{
	u8 retry=0;				 	
	while (SPI_I2S_GetFlagStatus(SPI2, SPI_I2S_FLAG_TXE) == RESET) //���ָ����SPI��־λ�������:���ͻ���ձ�־λ
		{
			retry++;
			if(retry>200)return 0;
		}			  
	SPI_I2S_SendData(SPI2, byte); //ͨ������SPIx����һ������
	retry=0;

	while (SPI_I2S_GetFlagStatus(SPI2, SPI_I2S_FLAG_RXNE) == RESET)//���ָ����SPI��־λ�������:���ܻ���ǿձ�־λ
		{
			retry++;
			if(retry>200)return 0;
		}	  

	byte = SPI_I2S_ReceiveData(SPI2);		
	return byte; //����ͨ��SPIx������յ�����
}

