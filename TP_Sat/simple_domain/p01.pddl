(define (problem simple-robot-1)
   (:domain simple-robot)
   (:objects rooma roomb)
   (:init 
        (room rooma)
        (room roomb)
        (at-robby rooma)
   )
   (:goal (and 
        (at-robby roomb)
    ))
)