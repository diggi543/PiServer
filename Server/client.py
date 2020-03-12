#!/usr/bin/env python3

import socket
import json

sock = socket.socket()
sock.connect(('192.168.0.1', 3333))
login = input('Login: ')
password = input('Password: ')
data = {}
data['login'] = login
data['password'] = password
sock.send(json.dumps(data).encode('UTF-8'))
response = sock.recv(1024).decode('UTF-8')
print(response)
response = json.loads(response)
if response['auth'] == True:
	while True:
		command = input('Command: ')
		data = {}
		data['command'] = command
		sock.send(json.dumps(data).encode('UTF-8'))
		response = sock.recv(1024).decode('UTF-8')
		print(response)