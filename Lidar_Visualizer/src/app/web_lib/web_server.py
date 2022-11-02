from http.server import HTTPServer, BaseHTTPRequestHandler
import os

class web_server():

    def __init__(self, port):
        httpd = HTTPServer(('localhost', port), self.serv)
        httpd.serve_forever()

    class serv(BaseHTTPRequestHandler):
        def do_GET(self):
            indexName = 'index.html'
            self.path = os.path.dirname(os.path.abspath(__file__)) + "\\web\\" + indexName
            try:
                file_to_open = open(self.path).read()
                self.send_response(200)
            except:
                file_to_open = "File not found"
                self.send_response(404)
            self.end_headers()
            self.wfile.write(bytes(file_to_open, 'utf-8'))


web_server(8080)