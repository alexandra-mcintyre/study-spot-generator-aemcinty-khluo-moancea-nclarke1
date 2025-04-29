import React from 'react'
import { useState } from 'react'
import axios from "axios";

/**
 * This is the `Form` component, which represents a form for user input.
 * Users can input a distance value, which is then sent to a server using Axios.
 *
 * @returns {JSX.Element} The JSX element representing the `Form` component.
 */
export default function form() {
    const [dist, setDist] = useState(0)

    /**
     * Handles the form submission by sending the distance value to the server
     * using Axios and reloading the page.
     *
     * @param {Event} e - The form submission event.
     */
    const handleSubmit = (e) => {
        e.preventDefault()

        axios.post('http://localhost:8080/dist', {
            distance : dist
        })

        window.location.reload();
       
    }

    return (
        <div className="createform">
            <form onSubmit={handleSubmit}>
                <label>Distance (miles) between 0 - 30:</label>
                <input 
                    type="number" 
                    required
                    value = {dist}
                    onChange = {(e) => setDist(e.target.value)}
                ></input>
                <button> Submit </button>
            </form>
        </div>
    )
}