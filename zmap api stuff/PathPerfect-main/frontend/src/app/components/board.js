/**
 * This is the `Board` component that represents a board in your React application.
 * It displays information about "PathPerfect" and provides some details about
 * the jogging path finder.
 *
 * @returns {JSX.Element} The JSX element representing the `Board` component.
 */
import React from 'react'

export default function Board() {
    return (
        <div className = "board">
            <h1 className = "leaderboard">PathPerfect</h1>
            <div className = "hRow">
                🏃 Jogging path finder based on distance to travel
                <br></br>
                <p style={{fontSize:"22px"}}> 📍 Scroll down to see how it works</p>


            </div>
            
        </div>
    )
}