#ifndef __KEY_H
#define __KEY_H	 
#include "sys.h"

#define WK_UP   GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_0)//��ȡ����2 

#define WKUP_PRES	3		//WK_UP  

void KEY_Init(void);//IO��ʼ��
u8 KEY_Scan(u8 mode);  	//����ɨ�躯��					    
#endif
