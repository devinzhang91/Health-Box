#include "LCD.h"
#include "ascii.h"
#include "delay.h"
#include "touch.h" 

typedef struct
{
  vu16 LCD_REG;
  vu16 LCD_RAM;
} LCD_TypeDef;

/* Note: LCD /CS is CE4 - Bank 4 of NOR/SRAM Bank 1~4 */
#define LCD_BASE        ((u32)(0x60000000 | 0x0C000000))
#define LCD             ((LCD_TypeDef *) LCD_BASE)

u16  DeviceCode;
/***************************LCD��ʼ��*******************************/
void LCD_Init(void)
{
	LCD_guanjiao();
	LCD_FSMC();

	DeviceCode = LCD_ReadReg(0x0000);	 //��ʹ�ö��LCDʱ��ȡ�豸�벢ѡ��

		LCD_WriteReg(0x00E5,0x78F0); 
		LCD_WriteReg(0x0001,0x0100); 
		LCD_WriteReg(0x0002,0x0700); 
		LCD_WriteReg(0x0003,0x1030); 
		LCD_WriteReg(0x0004,0x0000); 
		LCD_WriteReg(0x0008,0x0202);  
		LCD_WriteReg(0x0009,0x0000);
		LCD_WriteReg(0x000A,0x0000); 
		LCD_WriteReg(0x000C,0x0000); 
		LCD_WriteReg(0x000D,0x0000);
		LCD_WriteReg(0x000F,0x0000);
		//power on sequence VGHVGL
		LCD_WriteReg(0x0010,0x0000);   
		LCD_WriteReg(0x0011,0x0007);  
		LCD_WriteReg(0x0012,0x0000);  
		LCD_WriteReg(0x0013,0x0000); 
		LCD_WriteReg(0x0007,0x0000); 
		//vgh 
		LCD_WriteReg(0x0010,0x1690);   
		LCD_WriteReg(0x0011,0x0227);
		delay_ms(100);
		//vregiout 
		LCD_WriteReg(0x0012,0x009D); //0x001b
		delay_ms(100); 
		//vom amplitude
		LCD_WriteReg(0x0013,0x1900);
		delay_ms(100); 
		//vom H
		LCD_WriteReg(0x0029,0x0025); 
		LCD_WriteReg(0x002B,0x000D); 
		//gamma
		LCD_WriteReg(0x0030,0x0007);
		LCD_WriteReg(0x0031,0x0303);
		LCD_WriteReg(0x0032,0x0003);// 0006
		LCD_WriteReg(0x0035,0x0206);
		LCD_WriteReg(0x0036,0x0008);
		LCD_WriteReg(0x0037,0x0406); 
		LCD_WriteReg(0x0038,0x0304);//0200
		LCD_WriteReg(0x0039,0x0007); 
		LCD_WriteReg(0x003C,0x0602);// 0504
		LCD_WriteReg(0x003D,0x0008); 
		//ram
		LCD_WriteReg(0x0050,0x0000); 
		LCD_WriteReg(0x0051,0x00EF);
		LCD_WriteReg(0x0052,0x0000); 
		LCD_WriteReg(0x0053,0x013F);  
		LCD_WriteReg(0x0060,0xA700); 
		LCD_WriteReg(0x0061,0x0001); 
		LCD_WriteReg(0x006A,0x0000); 
		//
		LCD_WriteReg(0x0080,0x0000); 
		LCD_WriteReg(0x0081,0x0000); 
		LCD_WriteReg(0x0082,0x0000); 
		LCD_WriteReg(0x0083,0x0000); 
		LCD_WriteReg(0x0084,0x0000); 
		LCD_WriteReg(0x0085,0x0000); 
		//
		LCD_WriteReg(0x0090,0x0010); 
		LCD_WriteReg(0x0092,0x0600); 
		
		LCD_WriteReg(0x0007,0x0133);
		LCD_WriteReg(0x00,0x0022);//
	
}
/**************************������д�뱳����ɫ**********************/
void LCD_Clear(u16 Color)		  
{
  u32 index = 0;
  LCD_SetCursor(0,0); 
  LCD_WriteRAM_Prepare(); /* Prepare to write GRAM */

  for(index = 0; index < 76800; index++)
  {
    LCD->LCD_RAM = Color;
  }  
}
/******************************׼����Һ��д������*********************/
void LCD_WriteRAM_Prepare(void)		   
{
  LCD->LCD_REG = 0x22;
}

/**************************��ȡLCDע����**************************************/
u16 LCD_ReadReg(u16 LCD_Reg)				 
{
  LCD->LCD_REG = LCD_Reg;		 //д��16λ����
  LCD->LCD_RAM;
  return (LCD->LCD_RAM);
}
/**************************��LCD����ѡ��REGд������****************************/
void LCD_WriteReg(u8 LCD_Reg, u16 LCD_RegValue)	 
{
  LCD->LCD_REG = LCD_Reg;		//д��Ĵ�����ַ
  LCD->LCD_RAM = LCD_RegValue;	//д������
}
/**************************��LCD����ѡ��RAMд������****************************/
void LCD_WriteRAM(u16 LCD_RegValue)	 
{
  LCD->LCD_RAM = LCD_RegValue;	//д������
}
/**********************�ܽ�����**********************************************/
void LCD_guanjiao(void)
{
	u16 a=1000;
	GPIO_InitTypeDef GPIO_InitStructure;

  RCC_AHBPeriphClockCmd(RCC_AHBPeriph_FSMC, ENABLE);

  RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOD | RCC_APB2Periph_GPIOE |
                         RCC_APB2Periph_GPIOF | RCC_APB2Periph_GPIOG ,ENABLE);

  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0 | GPIO_Pin_1 | GPIO_Pin_4 | GPIO_Pin_5 |
                                GPIO_Pin_8 | GPIO_Pin_9 | GPIO_Pin_10 | GPIO_Pin_14 | 
                                GPIO_Pin_15;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
  GPIO_Init(GPIOD, &GPIO_InitStructure);

  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_6 | GPIO_Pin_7 | GPIO_Pin_8 | GPIO_Pin_9 | GPIO_Pin_10 | 
                                GPIO_Pin_11 | GPIO_Pin_12 | GPIO_Pin_13 | GPIO_Pin_14 | 
                                GPIO_Pin_15;
  GPIO_Init(GPIOE, &GPIO_InitStructure);

  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0;
  GPIO_Init(GPIOF, &GPIO_InitStructure);

  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12;
  GPIO_Init(GPIOG, &GPIO_InitStructure);

  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_11;		  
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
  GPIO_Init(GPIOD, &GPIO_InitStructure);

  GPIO_ResetBits(GPIOD, GPIO_Pin_11);  //�ܽŸ�λ
  while(a--);
  GPIO_SetBits(GPIOD, GPIO_Pin_11);
}
/******************************FSMC����*********************************/
void LCD_FSMC()
{
    FSMC_NORSRAMInitTypeDef  FSMC_NORSRAMInitStructure;
    FSMC_NORSRAMTimingInitTypeDef  p; 
    
    
    p.FSMC_AddressSetupTime = 0x02;	 //��ַ����ʱ��
    p.FSMC_AddressHoldTime = 0x00;	 //��ַ����ʱ��
    p.FSMC_DataSetupTime = 0x05;		 //���ݽ���ʱ��
    p.FSMC_BusTurnAroundDuration = 0x00;
    p.FSMC_CLKDivision = 0x00;
    p.FSMC_DataLatency = 0x00;
    p.FSMC_AccessMode = FSMC_AccessMode_B;	 // һ��ʹ��ģʽB������LCD
    
    FSMC_NORSRAMInitStructure.FSMC_Bank = FSMC_Bank1_NORSRAM4;
    FSMC_NORSRAMInitStructure.FSMC_DataAddressMux = FSMC_DataAddressMux_Disable;
    //FSMC_NORSRAMInitStructure.FSMC_MemoryType = FSMC_MemoryType_SRAM;
		FSMC_NORSRAMInitStructure.FSMC_MemoryType = FSMC_MemoryType_NOR;
    FSMC_NORSRAMInitStructure.FSMC_MemoryDataWidth = FSMC_MemoryDataWidth_16b;
    FSMC_NORSRAMInitStructure.FSMC_BurstAccessMode = FSMC_BurstAccessMode_Disable;
    FSMC_NORSRAMInitStructure.FSMC_WaitSignalPolarity = FSMC_WaitSignalPolarity_Low;
    FSMC_NORSRAMInitStructure.FSMC_WrapMode = FSMC_WrapMode_Disable;
    FSMC_NORSRAMInitStructure.FSMC_WaitSignalActive = FSMC_WaitSignalActive_BeforeWaitState;
    FSMC_NORSRAMInitStructure.FSMC_WriteOperation = FSMC_WriteOperation_Enable;
    FSMC_NORSRAMInitStructure.FSMC_WaitSignal = FSMC_WaitSignal_Disable;
    FSMC_NORSRAMInitStructure.FSMC_ExtendedMode = FSMC_ExtendedMode_Disable;
    FSMC_NORSRAMInitStructure.FSMC_WriteBurst = FSMC_WriteBurst_Disable;
    FSMC_NORSRAMInitStructure.FSMC_ReadWriteTimingStruct = &p;
    FSMC_NORSRAMInitStructure.FSMC_WriteTimingStruct = &p;  
    
    FSMC_NORSRAMInit(&FSMC_NORSRAMInitStructure); 
    
    /* ʹ�� FSMC Bank1_SRAM Bank */
    FSMC_NORSRAMCmd(FSMC_Bank1_NORSRAM4, ENABLE);  
}

/****************************************************************************
* ��    �ƣ�void LCD_SetPoint(u16 x,u16 y,u16 point)
* ��    �ܣ���ָ�����껭��
* ��ڲ�����x      ������
*           y      ������
*           point  �����ɫ
* ���ڲ�������
* ˵    ����
* ���÷�����LCD_SetPoint(10,10,0x0fe0);
****************************************************************************/
void LCD_SetPoint(u16 x,u16 y,u16 point)
{
  if ( (x>320)||(y>240) ) return;
  LCD_SetCursor(x,y);

  LCD->LCD_REG=0x22;;
  LCD->LCD_RAM=point;
}
/****************************************************************************
* ��    �ƣ�void LCD_SetCursor(u16 x,u16 y)
* ��    �ܣ�������Ļ����
* ��ڲ�����x      ������
*           y      ������
* ���ڲ�������
* ˵    ����
* ���÷�����LCD_SetCursor(10,10);
****************************************************************************/
__inline void LCD_SetCursor(u16 x,u16 y)
{
    LCD_WriteReg(0x0020,y);        //��
  	LCD_WriteReg(0x0021,0x13f-x);  //��
}

/****************************************************************************
* ��    �ƣ�u16 LCD_GetPoint(u16 x,u16 y)
* ��    �ܣ���ȡָ���������ɫֵ
* ��ڲ�����x      ������
*           y      ������
* ���ڲ�������ǰ������ɫֵ
* ˵    ����
* ���÷�����i=LCD_GetPoint(10,10);
****************************************************************************/
u16 LCD_GetPoint(u16 x,u16 y)
{
  LCD_SetCursor(x,y);
  if(DeviceCode==0x7783)
    return (LCD_ReadRAM());
  else
   return 
 	(LCD_BGR2RGB(LCD_ReadRAM()));
}

/****************************************************************************
* ��    �ƣ�u16 LCD_BGR2RGB(u16 c)
* ��    �ܣ�RRRRRGGGGGGBBBBB ��Ϊ BBBBBGGGGGGRRRRR ��ʽ
* ��ڲ�����c      BRG ��ɫֵ
* ���ڲ�����RGB ��ɫֵ
* ˵    �����ڲ���������
* ���÷�����
****************************************************************************/
u16 LCD_BGR2RGB(u16 c)
{
  u16  r, g, b, rgb;

  b = (c>>0)  & 0x1f;
  g = (c>>5)  & 0x3f;
  r = (c>>11) & 0x1f;
  
  rgb =  (b<<11) + (g<<5) + (r<<0);

  return( rgb );
}

u16 LCD_ReadRAM(void)
{
  vu16 dummy;
  /* Write 16-bit Index (then Read Reg) */
  LCD->LCD_REG = 0x22; /* Select GRAM Reg */
  /* Read 16-bit Reg */
  dummy = LCD->LCD_RAM; 
  return LCD->LCD_RAM;
}

/****************************************************************************
* ��    �ƣ�LCD_DrawLine(u16 x1, u16 y1, u16 x2, u16 y2,u16 point)
* ��    �ܣ���ָ��������������
* ��ڲ�����	x1          	���������
*           y1          	���������
*						x2          	�յ�������
*           y2          	�յ�������
*						point					�ߵ���ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ����
* ���÷�����LCD_DrawLine(0, 0, 10, 10,0xffff)
****************************************************************************/
void LCD_DrawLine(u16 x1, u16 y1, u16 x2, u16 y2,u16 pointColor)
{
	u16 t; 
	int xerr=0,yerr=0,delta_x,delta_y,distance; 
	int incx,incy,uRow,uCol; 

	delta_x=x2-x1; //������������ 
	delta_y=y2-y1; 
	uRow=x1; 
	uCol=y1; 
	if(delta_x>0)incx=1; //���õ������� 
	else if(delta_x==0)incx=0;//��ֱ�� 
	else {incx=-1;delta_x=-delta_x;} 
	if(delta_y>0)incy=1; 
	else if(delta_y==0)incy=0;//ˮƽ�� 
	else{incy=-1;delta_y=-delta_y;} 
	if( delta_x>delta_y)distance=delta_x; //ѡȡ�������������� 
	else distance=delta_y; 
	for(t=0;t<=distance+1;t++ )//������� 
	{  
		LCD_SetPoint(uRow,uCol,pointColor);//���� 
		xerr+=delta_x ; 
		yerr+=delta_y ; 
		if(xerr>distance) 
		{ 
			xerr-=distance; 
			uRow+=incx; 
		} 
		if(yerr>distance) 
		{ 
			yerr-=distance; 
			uCol+=incy; 
		} 
	}  
}

/****************************************************************************
* ��    �ƣ�LCD_DrawRectangle(u16 x1, u16 y1, u16 x2, u16 y2,u16 point)
* ��    �ܣ���ָ�������Խ����껭����
* ��ڲ�����	x1          	���������
*           y1          	���������
*						x2          	�յ�������
*           y2          	�յ�������
*						point					���ε���ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ�ľ���
* ���÷�����LCD_DrawRectangle(0, 0, 10, 10,0xffff)
****************************************************************************/
void LCD_DrawRectangle(u16 x1, u16 y1, u16 x2, u16 y2,u16 pointColor)
{
	LCD_DrawLine(x1,y1,x2,y1,pointColor);
	LCD_DrawLine(x1,y1,x1,y2,pointColor);
	LCD_DrawLine(x1,y2,x2,y2,pointColor);
	LCD_DrawLine(x2,y1,x2,y2,pointColor);
}

/****************************************************************************
* ��    �ƣ�LCD_Draw_Circle(u16 x0,u16 y0,u8 r,u16 point)
* ��    �ܣ���ָ��Բ�Ļ���ָ���뾶��Բ
* ��ڲ�����	x          	������
*           y          	������
*           r						Բ�İ뾶
*						point				Բ����ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ��Բ
* ���÷�����LCD_Draw_Circle(100, 100, 20)
****************************************************************************/
void LCD_Draw_Circle(u16 x0,u16 y0,u8 r,u16 pointColor)
{
	int a,b;
	int di;
	a=0;b=r;	  
	di=3-(r<<1);             //�ж��¸���λ�õı�־
	while(a<=b)
	{
		LCD_SetPoint(x0-b,y0-a, pointColor);             //3           
		LCD_SetPoint(x0+b,y0-a, pointColor);             //0           
		LCD_SetPoint(x0-a,y0+b, pointColor);             //1       
		LCD_SetPoint(x0-b,y0-a, pointColor);             //7           
		LCD_SetPoint(x0-a,y0-b, pointColor);             //2             
		LCD_SetPoint(x0+b,y0+a, pointColor);             //4               
		LCD_SetPoint(x0+a,y0-b, pointColor);             //5
		LCD_SetPoint(x0+a,y0+b, pointColor);             //6 
		LCD_SetPoint(x0-b,y0+a, pointColor);             
		a++;
		//ʹ��Bresenham�㷨��Բ     
		if(di<0)di +=4*a+6;	  
		else
		{
			di+=10+4*(a-b);   
			b--;
		} 
		LCD_SetPoint(x0+a,y0+b, pointColor);
	}
}

void LCD_Draw_Button(u16 x, u16 y, u8 type)
{
	if(type==0)
	{
		LCD_Draw_Circle(x+10, y+10, 10, Black);
		LCD_Draw_Circle(x+60, y+10, 10, Black);
		LCD_Draw_Circle(x+10, y+40, 10, Black);
		LCD_Draw_Circle(x+60, y+40, 10, Black);
		LCD_DrawLine(x+10,y,x+60,y,Black);
		LCD_DrawLine(x+10,y+50,x+60,y+50,Black);
		LCD_DrawLine(x,y+10,x,y+40,Black);
		LCD_DrawLine(x+70,y+10,x+70,y+40,Black);
		LCD_Color_Fill(x+11, y+1, x+59, y+49,White);
		LCD_Color_Fill(x+1, y+11, x+69, y+39,White);
	} 
	else if(type==1)
	{
	}
	else if(type==2)
	{
	}
	else if(type==3)
	{
	}
}

/****************************************************************************
* ��    �ƣ�LCD_Draw_Target(u16 x0,u16 y0,u16 pointColor)
* ��    �ܣ���ָ�����껭һ��Ŀ��ͼ��
* ��ڲ�����	x          	������
*           y          	������
*						point				Բ����ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ
* ���÷�����LCD_Draw_Target(100, 100, 0xffff)
****************************************************************************/
void LCD_Draw_Target(u16 x,u16 y,u16 pointColor)
{
	LCD_DrawLine(x-12, y, x+12, y , pointColor);
	LCD_DrawLine(x, y-12, x, y+12, pointColor);
	LCD_Draw_Circle(x, y, 8, pointColor);
}

/****************************************************************************
* ��    �ƣ�void LCD_printString(u16 x,u16 y, u8 *ptr, u16 )
* ��    �ܣ���ָ��������ʾһ��8x16�����ascii�ַ���
* ��ڲ�����	x          	������
*           y          	������
*           ptr  				�ַ�����
*           charColor		�ַ���ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ��ascii��
* ���÷�����LCD_printString(10,10,"hello world ", 0xffff);
****************************************************************************/
void LCD_printString(u16 x,u16 y, u8 *ptr, u16 charColor)
{
  u32 i = 0;
  u16 refcolumn = x;
  while ((*ptr != 0) & (i < 120))
  {
    LCD_ShowChar(refcolumn, y, *ptr, charColor);
		refcolumn += 6;
    ptr++;
    i++;
 }
}

/****************************************************************************
* ��    �ƣ�void LCD_ShowChar(u16 x,u16 y, u32 c, u16 charColor)
* ��    �ܣ���ָ��������ʾһ��8x16�����ascii�ַ�
* ��ڲ�����	x          	������
*           y          	������
*           ptr  				�ַ�����
*           charColor		�ַ���ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ��ascii��
* ���÷�����LCD_printString(10,10,'a', 0xffff);
****************************************************************************/
void LCD_ShowChar(u16 x,u16 y,u32 c, u16 charColor)
{  							  
	u8 temp,t1,t;
	u16 y0=y;
	u8 csize=12;		//�õ�����һ���ַ���Ӧ������ռ���ֽ���	
	//���ô���		   
	c=c-' ';//�õ�ƫ�ƺ��ֵ
	for(t=0;t<csize;t++)
	{   
		temp=asc2_1206[c][t]; 	 	//����1206����
		for(t1=0;t1<8;t1++)
		{			    
			if(temp&0x80)LCD_SetPoint(x,y,charColor);
			temp<<=1;
			y++;
			if(x>=320)return;		//��������
			if((y-y0)==12)
			{
				y=y0;
				x++;
				if(x>=320)return;	//��������
				break;
			}
		}  	 
	}  	
} 

/****************************************************************************
* ��    �ƣ�void LCD_ShowNum(u16 x,u16 y,u32 num,u16 charColor)
* ��    �ܣ���ָ��������ʾһ������
* ��ڲ�����	x          	������
*           y          	������
*           num  				�ַ�����
*           charColor		�ַ���ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ��32λ��
* ���÷�����void LCD_ShowNum(5,5,0xffff);
****************************************************************************/
void LCD_ShowNum(u16 x,u16 y,u32 num,u16 charColor)
{
	u8 numChar[12]={0};			//���num�ַ���
	sprintf((char*)numChar,"%d",num); //��num��ӡ��numChar���顣
	LCD_printString(x,y,numChar,charColor);
}

/****************************************************************************
* ��    �ƣ�void LCD_Color_Fill(u16 sx,u16 sy,u16 ex,u16 ey,u16 *color)
* ��    �ܣ���ָ�����������ָ����ɫ��
* ��ڲ�����	x1          	���������
*           y1          	���������
*						x2          	�յ�������
*           y2          	�յ�������
*						color					���ε���ɫ
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ��32λ��
* ���÷�����void LCD_Color_Fill(0,0,100,100,0xffff);
****************************************************************************/
void LCD_Color_Fill(u16 sx,u16 sy,u16 ex,u16 ey,u16 color)
{  
	u16 height,width;
	u16 i,j;
	width=ex-sx+1; 			//�õ����Ŀ��
	height=ey-sy+1;			//�߶�
 	for(i=0;i<width;i++)
	{
 		LCD_SetCursor(sx+i,sy);   	//���ù��λ�� 
		LCD_WriteRAM_Prepare();     //��ʼд��GRAM
		for(j=0;j<height;j++) LCD->LCD_RAM = color;//д������ 
	}	  
}


/****************************************************************************
* ��    �ƣ�void LCD_WriteBMP( u16 Height, u16 Width, u8 *bitmap)
* ��    �ܣ���ָ�����������ָ����С��ͼƬ
* ��ڲ�����	x		          	���������
*						y		          	���������
*						Height          λͼ�ĸ߶�
*           Width          	λͼ�Ŀ��
*						bitmap          λͼ��ָ��
* ���ڲ�������
* ˵    ������ʾ��Χ�޶�Ϊ����ʾ��32λ��
* ���÷�����LCD_WriteBMP( 240, 320,  *pic)��
****************************************************************************/
// void LCD_WriteBMP(u16 x, u16 y, u16 height, u16 width, u8 *bitmap)
// {
//   u16 i,j;
//   u16 *bitmap_ptr = (u16 *)bitmap;

// 	for(i=0;i<width+1;i++)
// 	{
//  		LCD_SetCursor(x+i,y);   	//���ù��λ�� 
// 		LCD_WriteRAM_Prepare();     //��ʼд��GRAM
// 		for(j=0;j<height+1;j++) 
// 		{
// 			LCD->LCD_RAM = bitmap_ptr[i*height+j];//д������
// 		}
// 	}
// }

void LCD_WriteFullBMP(u8 *bitmap)
{
  vu32 index;
  u16 *bitmap_ptr = (u16 *)bitmap;

//  LCD_WriteReg(0x03, 0x1038);	  //��Ļ�����෴
 
  for(index = 0; index < 76800; index++)
  {
		LCD->LCD_RAM = (*bitmap_ptr++);
  }
}




//�û���Ӱ�ť
void Add_Button(void)
{
  LCD_Clear(White);		 //д�뱳����ɫ
	
	LCD_DrawRectangle(40,140,120,200, Blue);
	LCD_DrawRectangle(39,139,121,201, Blue);
	LCD_printString(65,165, "DAC Up" ,Black);
	
	LCD_DrawRectangle(200,140,280,200, Blue);
	LCD_DrawRectangle(199,139,281,201, Blue);
	LCD_printString(220,165, "DAC Down" ,Black);
	
	LCD_printString(20,10, "DAC:      mV" ,Black);
	LCD_printString(120,10, "ADC:      mV" ,Black);
}



