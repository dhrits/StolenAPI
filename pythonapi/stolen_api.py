import requests

from errors import *

_BASE_URL = 'https://api.getstolen.com/api'


"""This is important. Must obtain unique ID using MITM Proxy"""
with open('unique_id.txt', 'r') as fd:
    _UNIQUE_ID = fd.read().strip()


class _StolenApi(object):
    """ Encapsulates the most common functionality of the stolen app """
    def __init__(self, unique_id=None):
        self.unique_id = unique_id
        if not self.unique_id:
            self.unique_id = _UNIQUE_ID

        self.headers = {
            'host': 'api.getstolen.com',
            'Connection': 'keep-alive',
            'Proxy-Connection': 'keep-alive',
            'Accept': '*/*',
            'User-Agent': 'Stolen/293 CFNetwork/758.2.8 Darwin/15.0.0',
            'Accept-Language': 'en-us',
            'Authorization': 'Bearer ' + self.unique_id,
            'Accept-Encoding': 'gzip, deflate',
        }
        self.session = requests.Session()
        self.session.headers.update(self.headers)

    def get(self, path, **params):
        if not path.startswith('/'):
            path = '/' + path
        resp = self.session.get(_BASE_URL + path, params=params)
        if resp.status_code != 200:
            raise InvalidRequest(resp.status_code)
        return resp.json().get('data')        

    def post(self, path, **params):
        if not path.startswith('/'):
            path = '/' + path
        resp = self.session.post(_BASE_URL + path, data=params)
        if resp.status_code != 200:
            raise InvalidRequest(resp.status_code)
        return resp.json().get('data')        
        
    def put(self, path, **params):
        if not path.startswith('/'):
            path = '/' + path
        resp = self.session.put(_BASE_URL + path, data=params)
        if resp.status_code != 200:
            raise InvalidRequest(resp.status_code)
        return resp.json().get('data')        

    def head(self, path):
        if not path.startswith('/'):
            path = '/' + path
        resp = self.session.head(_BASE_URL + path)
        if resp.status_code != 200:
            raise InvalidRequest(resp.status_code)
        return resp.json().get('data')        

    def delete(self, path):
        if not path.startswith('/'):
            path = '/' + path
        resp = self.session.delete(_BASE_URL + path)
        if resp.status_code != 200:
            raise InvalidRequest(resp.status_code)
        return resp.json().get('data')        

    def options(self, path):
        if not path.startswith('/'):
            path = '/' + path
        resp = self.session.options(_BASE_URL + path)
        if resp.status_code != 200:
            raise InvalidRequest(resp.status_code)
        return resp.json().get('data')

    # Methods to allow basic access

    def get_me(self):
        return self.get('/me')

    def get_wall(self, person_id, limit=20, since_id=None):
        return self.get('/people/{}/wall'.format(person_id), limit=limit, since_id=since_id)

    def get_my_notifications(self, limit=20, since_id=None):
        return self.get('/me/notifications', limit=limit, since_id=since_id)

    def get_my_activity(self, limit=20, since_id=None):
        return self.get('/me/activity', limit=limit, since_id=since_id)

    def get_my_vault(self):
        return self.get('/me/vault')

    def get_my_invite_codes(self, before_id=None, limit=None):
        return self.get('/me/invitecodes', before_id=before_id, limit=limit)

    def get_person(self, person_id):
        return self.get('/people/{}'.format(person_id))

    def get_persons_pets(self, person_id, before_id=None, limit=20):
        return self.get('/people/{}/pets'.format(person_id), before_id=before_id, limit=limit)

    def get_people(self, before_id=None, limit=None):
        return self.get('/people')

    def get_recently_stolen(self, before_id=None, limit=20):
        return self.get('/lists/recently_stolen', limit=limit)

    def get_new(self, before_id=None, limit=20):
        return self.get('/lists/new', limit=limit)

    def get_top(self, before_id=None, limit=20):
        return self.get('/lists/value', limit=limit)

    def get_friends(self, before_id=None, limit=20):
        return self.get('/me/friends', limit=limit)

    def buy(self, person_id, purchase_uuid):
        return self.post('/people/{}/buy'.format(person_id), purchase_uuid=purchase_uuid)

    def get_my_id(self):
        me = self.get_me()
        return me['id']

    def get_my_balance(self):
        me = self.get_me()
        return me['wallet']['balance']


api = _StolenApi()
