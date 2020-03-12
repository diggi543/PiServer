#!/usr/bin/env python

import socket
from threading import Thread
import json
import subprocess
import module_neon as neon
import pihome
import re

alredyConnect = False

class Colorize(object):

	@staticmethod
	def printError(text):
		print("\033[1;31m" + text + "\033[0;0m")

	@staticmethod
	def printGood(text):
		print("\033[1;32m" + text + "\033[0;0m")

	@staticmethod
	def printWarn(text):
		print("\033[1;33m" + text + "\033[0;0m")

class MySocket(object):
	sock = None

	@staticmethod
	def create(addr, port):
		MySocket.sock = socket.socket()
		MySocket.sock.bind((addr, port))
		MySocket.sock.listen(1)

	@staticmethod
	def wait():
		return MySocket.sock.accept()

	@staticmethod
	def close():
		Colorize.printWarn("Close socket...")
		MySocket.sock.close()

class Data(object):
	data = {}

	@staticmethod
	def put(key, value):
		Data.data[key] = value

	@staticmethod
	def toJson():
		jsonData = json.dumps(Data.data)
		Data.data = {}
		return jsonData

class Client(object):
	conn = None
	addr = None

	@staticmethod
	def onUserConnected(conn, addr):
		global alredyConnect
		alredyConnect = True
		Client.conn = conn
		Client.addr = addr
		# print('Get connection from ' + str(addr))
		try:
			if Client.authenticate():
				Client.wait()
		except Exception:
			pass
		alredyConnect = False
		Client.conn.close()

	@staticmethod
	def authenticate():
		count = 0
		authenticated = False
		while count < 3 and not authenticated:
			data = Client.readUTF8(1024)
			dataDict = json.loads(data)
			if 'login' in dataDict and 'password' in dataDict:
				login = dataDict['login']
				password = dataDict['password']
				authenticated = Client.checkCredentials(login, password)
			else:
				Client.unknownCommand()
			count = count + 1
		return authenticated

	@staticmethod
	def checkCredentials(login, password):
		validate = (login == 'admin' and password == 'smarthome')
		Data.put('auth', validate)
		Client.send(Data.toJson())
		return validate

	@staticmethod
	def wait():
		while Client.conn:
			data = Client.readUTF8(2048)
			if not data:
				break
			Client.parseData(data)

	@staticmethod
	def parseData(data):
		dataDict = json.loads(data)
		if 'command' in dataDict:
			command = dataDict['command']
			if command == 'getInfo':
				Client.sendInfo()
			elif command == 'turnOnLight':
				neon.on()
				Client.sendOk()
			elif command == 'turnOffLight':
				neon.off()
				Client.sendOk()
			elif command.startswith('change_mode_'):
				Client.changeStatePiHome(command)
			else:
				Client.unknownCommand()
		else:
			Client.unknownCommand()

	@staticmethod
	def sendInfo():
		info = subprocess.check_output(['uname', '-a']).decode('UTF-8')
		Data.put('info', info)
		Client.send(Data.toJson())

	@staticmethod
	def sendOk():
		Data.put('response', 'OK')
		Client.send(Data.toJson())

	@staticmethod
	def changeStatePiHome(command):
		mode = re.search('mode_(.*?)_', command).group(1)
		state = re.search(mode + '_(.*?)$', command).group(1)
		state = (state == 'true')
		print('State: ' + str(state))
		pihome.changeMode(mode, state)
		Data.put('notification', 'Mode ' + mode + ' changed to ' + str(state))
		Client.send(Data.toJson())

	@staticmethod
	def unknownCommand():
		Data.put('error', 'Unknown command received')
		Client.send(Data.toJson())		

	@staticmethod
	def send(data):
		# print('Send: ' + data)
		Client.conn.send((data + '\n').encode('UTF-8'))

	@staticmethod
	def readUTF8(size):
		receive = Client.conn.recv(size).decode('UTF-8')
		# print('Receive: ' + receive)
		return receive

MySocket().create('', 3333)

try:
	print('[*] PiHome server start!')
	while True:
		conn, addr = MySocket.wait()
		if alredyConnect:
			Data.put('error', 'Another user alredy connected')
			conn.send(Data.toJson().encode('UTF-8'))
			conn.close()
		else:
			clientThread = Thread(target=Client.onUserConnected, args=(conn, addr))
			clientThread.start()
except KeyboardInterrupt as e:
	Colorize.printWarn("User interrupt process!")
	pihome.onUserInterrupt()
	MySocket.close()