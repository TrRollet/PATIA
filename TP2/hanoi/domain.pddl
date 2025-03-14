;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Tours de Hanoi
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define (domain hanoi)
  (:requirements :strips :typing)  ; On enlève disjunctive-preconditions

  (:types disc tige)
  (:predicates 
    (clear ?x - object)        ; Rien au-dessus
    (on ?x - disc ?y - object)    ; Position du disque
    (smaller ?x - disc ?y - disc) ; Relation de taille
    (handempty)                   ; Main vide (ajout)
    (holding ?x - disc)           ; Tenir un disque (ajout)
  )

  (:action pick-up
    :parameters (?x - disc ?t - tige)
    :precondition (and 
      (clear ?x)
      (on ?x ?t)
      (handempty)
    )
    :effect (and 
      (holding ?x)
			(clear ?t)
      (not (clear ?x))
      (not (on ?x ?t))
      (not (handempty))
    )
  )

  (:action put-down
    :parameters (?x - disc ?t - tige)
    :precondition (and
      (holding ?x)
      (clear ?t)
    )
    :effect (and
      (not (holding ?x))
      (handempty)
      (clear ?x)
      (on ?x ?t)
      (not (clear ?t))
    )
  )

  (:action stack
    :parameters (?x - disc ?y - disc)
    :precondition (and
      (holding ?x)
      (clear ?y)
      (smaller ?x ?y)
    )
    :effect (and
      (not (holding ?x))
      (handempty)
      (clear ?x)
      (on ?x ?y)
      (not (clear ?y))
    )
  )
  
  (:action unstack
    :parameters (?x - disc ?y - disc)
    :precondition (and
      (clear ?x)
      (on ?x ?y)
      (handempty)
    )
    :effect (and
      (holding ?x)
      (clear ?y)
      (not (clear ?x))
      (not (handempty))
      (not (on ?x ?y))
    )
  )
)
  
