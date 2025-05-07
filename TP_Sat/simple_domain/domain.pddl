(define (domain simple-robot)
    (:requirements :strips :typing)
    (:types room - place)
    
    (:predicates 
        (room ?r)           ; ?r est une pièce
        (at-robby ?r)       ; le robot est dans la pièce ?r
    )

    (:action move
        :parameters (?from ?to)
        :precondition (and 
            (room ?from)
            (room ?to)
            (at-robby ?from)
        )
        :effect (and 
            (at-robby ?to)
            (not (at-robby ?from))
        )
    )
)