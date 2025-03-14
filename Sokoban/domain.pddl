(define (domain sokoban)
    (:requirements :strips :typing)
    
    (:types 
        player box position
    )

    (:predicates
        (at-player ?p - player ?pos - position)
        (at-box ?b - box ?pos - position)
        (clear ?pos - position)
        (is-target ?pos - position)
        (adjacent ?from ?to - position)
        (is-accessible ?pos - position)
    )

    ;; Déplacement simple du joueur
    (:action move
        :parameters (?player - player ?from ?to - position)
        :precondition (and
            (at-player ?player ?from)
            (clear ?to)
            (adjacent ?from ?to)
            (is-accessible ?to)
        )
        :effect (and
            (not (at-player ?player ?from))
            (at-player ?player ?to)
            (not (clear ?to))
            (clear ?from)
            (is-accessible ?from)  ; La position quittée devient accessible
        )
    )

    ;; Pousser une boîte
    (:action push
        :parameters (?player - player ?box - box ?ppos ?bpos ?tpos - position)
        :precondition (and
            (at-player ?player ?ppos)
            (at-box ?box ?bpos)
            (clear ?tpos)
            (adjacent ?ppos ?bpos)
            (adjacent ?bpos ?tpos)
            (is-accessible ?tpos)
        )
        :effect (and
            (not (at-player ?player ?ppos))
            (not (at-box ?box ?bpos))
            (not (clear ?tpos))
            (at-player ?player ?bpos)
            (at-box ?box ?tpos)
            (clear ?ppos)
            (is-accessible ?ppos)
            (is-accessible ?bpos)  ; La position intermédiaire reste accessible
        )
    )
)