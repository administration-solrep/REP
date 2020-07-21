# -*- coding: utf-8 -*-
import random
import time

def extractToken(text, tag_start, tag_end):
    if text == None:
        return None
    start = text.find(tag_start)
    if start < 0:
        return None
    start = start + len(tag_start)
    end = text.find(tag_end, start)
    if end < 0:
        return None
    return text[start:end]

def extractJsfState(html):
    state = extractToken(html, '<input type="hidden" name="javax.faces.ViewState"'\
                 ' id="javax.faces.ViewState" value="', '"')
    if not state:
        raise ValueError('No JSF state found in the page.')
    if not state.startswith('j_id') or len(state)>10:
        raise ValueError('Invalid JSF State found: %s.' % str(state))
    return state


def extractLastStartToken(text, tag_start, tag_end):
    start = text.find(tag_start) 
    if start < 0:
        return None
    start = start + len(tag_start)
    end = text.find(tag_end, start)
    if end < 0:
           return None    
    prevstart = 0
    while start > prevstart and start < end:
        prevstart = start
        start = text.find(tag_start, start) 
        if start > 0:
            start = start + len(tag_start)
    return text[prevstart:end]
    
    
    
    
# randomTime
# Retourne un date éléatoirement choisie entre les dates 'start' et 'end'
# La date retournée, 'start' et 'end' sont des dates sous forme de chaine de caractères dont le format est 'format'
# 'format' est un format de date python strftime (ex : '%m/%d/%Y %I:%M %p')
# prop un nombre entre 0 et 1 utilisé pour le calcul aléatoire.
#   si prop = 0, alors la date retournée vaudra start
#   si prop = 1 alors la chaine retournée vaudra end
def randomTime(start, end, format, prop):

    stime = time.mktime(time.strptime(start, format))
    etime = time.mktime(time.strptime(end, format))

    ptime = stime + prop * (etime - stime)

    return time.strftime(format, time.localtime(ptime))
    
    
# retourne un tableau de 2 date situées entre les dates start et end.
# la premiere date du tableau est <= à la seconde
# toutes les dates suivent le format 'format' qui est au style python strftime
def randomDateInterval(start, end, format):
  rand1 = random.uniform(0,0.90)
  rand2 = random.uniform(0.10,0.5)
  randomTimedown = randomTime(start, end, format, rand1)
  randomTimeTop = randomTime(randomTimedown, end, format, rand2)
  
  return randomTimedown, randomTimeTop
 
  