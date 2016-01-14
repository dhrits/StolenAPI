# TODO(dman): Need better error support

class InvalidRequest(Exception):
    def __init__(self, code, message='Invalid Request'):
        super(InvalidRequest, self).__init__(message)
        self.code = code
        self.message = message
