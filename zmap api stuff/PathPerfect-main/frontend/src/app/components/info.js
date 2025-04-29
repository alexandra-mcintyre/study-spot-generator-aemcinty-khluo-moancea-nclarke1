import React from 'react'

/**
 * This is the `Info` component, which displays information about how the application works.
 *
 * @returns {JSX.Element} The JSX element representing the `Info` component.
 */
function info() {
  return (
    <div>
        <h1 className='info'>How it works</h1>
        <h2 className = 'info-description'>Based on the current location, our path-finding algorithm searches for possible paths in 3 directions. At every step, we search the estimated distance to head back; in other words, we always calculate the distance-so-far and the distance-to-go! </h2>
    </div>
  )
}

export default info