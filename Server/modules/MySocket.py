#!/usr/bin/env python3

import socket

sock = None

def create(addr, port):
	sock = socket.socket()
	sock.bind((addr, port))
	sock.listen(1)

def accept():
	sock.accept()

def printf():
	print(sock)

def readData(conn, size):
	return conn.recv(size).decode('UTF-8')

if __name__ == 'module':
	print("Main")