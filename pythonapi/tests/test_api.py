from ..stolen_api import api

#TODO(dman): Work in progress

def test_me():
    r = api.get_me()
    assert r is not None

def test_get_wall():
    my_id = api.get_my_id()
    r = api.get_wall(my_id)
    assert r is not None
