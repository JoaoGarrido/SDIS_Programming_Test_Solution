//NOT WORKING
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdint.h>
#include <errno.h>

#define MAX_REQUEST 10

int init_socket(int *sockfd, uint16_t port);

int main(int argc, char *argv[]) {
    int sockfd;
    uint16_t port;
    char avg[20];
    int error;

    //Read cmd
    if(argc < 3) {
        printf("./server <port> <average>\n");
        return -1;
    }
    error = sscanf(argv[1], "%hu", &port);
    if(error != 1){
        return -1;
    }
    strncpy(avg, argv[2], strlen(argv[2]));
    avg[strlen(argv[2])] = '\0';
    printf("%hu %s\n", port, argv[2]);

    //Init socket
    error = init_socket(&sockfd, port);
    if(error == -1){
        return -1;
    }

    //Server handler
    struct sockaddr clientaddr;
    socklen_t len = sizeof(clientaddr);
    while(1){
        //Accept
        error = accept(sockfd, &clientaddr, &len);
        if(error == -1){ //Should check ERRNO for further information
            printf("Accept failed %s\n", strerror(errno));
            fflush(stdout);
            break;
        }
        else{
            write(sockfd, (void*) avg, strlen(avg));
            printf("Sent temperature\n");
            fflush(stdout);
        }
    }

    //Close socket
    close(sockfd);
    printf("Socket closed\n");
    return 0;
}

int init_socket(int *sockfd, uint16_t port){
    struct sockaddr_in servaddr;
    int error;

    //Create
    *sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if(*sockfd == -1){
        printf("Error creating the socket\n");
        return -1;
    }
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port = htons(port);

    //Bind
    error = bind(*sockfd, (const struct sockaddr*) &servaddr, sizeof(servaddr));
    if(error != 0){
        printf("Bind failed\n");
        goto cleanup;
    }

    //Listen
    error = listen(*sockfd, MAX_REQUEST);
    if(error != 0){
        printf("Listen failed\n");
        goto cleanup;
    }

    printf("Socket created successfully\n");
    return 0;

cleanup:
    close(*sockfd);
    printf("Socket created unsuccessfully\n");
    return -1;
}