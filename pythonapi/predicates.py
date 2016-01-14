from people import Person

# Trading predicates
def is_verified(person):
    return person.verified

def is_cheap(person):
    return float(person.display_price) < 50000

# Halting predicates
def has_balance(person):
    return person.balance < 200000

def has_slots(person):
    return person.pet_slots < 6


