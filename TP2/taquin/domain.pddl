;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Jeu du taquin
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define (domain taquin)
  (:requirements :strips :typing)
  
  (:types 
    tile        ; Les tuiles numérotées
    position    ; Les positions sur la grille
  )

  (:predicates
    (at ?t - tile ?p - position)     ; Une tuile est à une position
    (blank ?p - position)            ; La case vide est à une position
    (adjacent ?p1 ?p2 - position)    ; Deux positions sont adjacentes
  )

  (:action move-tile
    :parameters (?t - tile ?from ?to - position)
    :precondition (and
      (at ?t ?from)           ; La tuile est à la position de départ
      (blank ?to)             ; La position d'arrivée est vide
      (adjacent ?from ?to)    ; Les positions sont adjacentes
    )
    :effect (and
      (not (at ?t ?from))     ; La tuile n'est plus à sa position initiale
      (not (blank ?to))       ; La position d'arrivée n'est plus vide
      (at ?t ?to)             ; La tuile est à sa nouvelle position
      (blank ?from)           ; L'ancienne position devient vide
    )
  )
)