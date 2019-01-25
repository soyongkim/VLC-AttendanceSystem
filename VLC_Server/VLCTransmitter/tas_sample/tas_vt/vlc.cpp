#include <stdio.h>
#include <string.h>

int turn_on(){
	puts("VT Light ON");
	return 0;
}

int turn_off(){
	puts("VT Light OFF");
	return 0;
}

int init(){
	puts("VT Init");
	return 0;
}

int main (int argc,char *argv[])
{
	int i;
    //for (i=0; i < argc; i++)
        //printf("Argument %d is %s\n", i, argv[i]);

	if(argc == 2){
		char* comm = argv[1];

		if(strcmp(comm, "on") == 0){
			turn_on();
		}else if(strcmp(comm, "off") == 0){
			turn_off();
		}else if(strcmp(comm, "0") == 0){
			init();
		}
	}

	return 0;
}
