//NOT WORKING
#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdint.h>

int init_socket(int *sockfd, const char *addr, uint16_t port);

int main(int argc, char *argv[]) {
    int sockfd;
    uint16_t port;
    int error;

    //Read cmd
    if(argc < 3) {
        printf("./bin/client <addr> <port>\n");
        return -1;
    }
    error = sscanf(argv[2], "%hu", &port);
    if(error != 1){
        return -1;
    }
    printf("%hu %s\n", port, argv[1]);

    //Init socket
    error = init_socket(&sockfd, (const char *) argv[1], port);
    if(error == -1){
        return -1;
    }

    //Client Handler
    const char msg[] = "GET/avg\0";
    int len;
    char buff[128];
    error = write(sockfd, (void*) msg, sizeof(msg));
    if(error == -1){
        printf("Error during write\n");
    }
    else {
        printf("Sent GET/avg request\n");
    }
    while(1) {
        len = read(sockfd, buff, sizeof(buff));
        buff[len] = '\0';
        if(len > 0){
            printf("%s", buff);
            fflush(stdout);
            break;
        }
        else if(len == -1){
            printf("Error occured\n");
            break;
        }
    }

    //Close socket
    close(sockfd);
    printf("Socket closed\n");
    return 0;
}

int init_socket(int *sockfd, const char *addr, uint16_t port){
    struct sockaddr_in servaddr;
    int error;

    //Create
    *sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if(*sockfd == -1){
        printf("Error creating the socket\n");
        return -1;
    }
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = inet_addr(addr);
    servaddr.sin_port = htons(port);

    //Connect
    error = connect(*sockfd, (const struct sockaddr*) &servaddr, sizeof(servaddr));
    if(error != 0){
        printf("Connect failed\n");
        goto cleanup;
    }

    printf("Socket created successfully\n");
    return 0;

cleanup:
    close(*sockfd);
    printf("Socket created unsuccessfully\n");
    return -1;
}