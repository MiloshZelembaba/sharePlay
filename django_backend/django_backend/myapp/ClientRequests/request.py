import socket
import json

def send_request(data, address, port):
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((address, int(port)))
        data = json.dumps(data)
        # s.sendall(len(data.encode('utf-8')))
        s.sendall(data + "\n")
        s.close()
    except Exception:
        print("pop")