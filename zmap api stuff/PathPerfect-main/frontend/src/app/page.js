"use client";
import Image from 'next/image'
import { useState } from "react";
import Board from './components/board'
import './components/style.css'
import Map from './components/Map'
import Form from './components/form'
import Topbar from './components/topbar'
import Info from './components/info'
import EndBar from './components/end';

/**
 * This is the `Home` component, representing the main page of your application.
 * It combines various components to create the user interface, including a top bar, board, map, form, information, and an end bar.
 *
 * @returns {JSX.Element} The JSX element representing the `Home` component.
 */
export default function Home() {
  return (
    <div>
      <Topbar></Topbar>
      <Board></Board>
      <div
      style={{
        marginTop: "20px",
        width: "100%",
        height: "70vh",
        
      }}
    >
      <div style={{width: "20%", float:"left", height:"100%"}}>
        <Form></Form>
      </div>
      <div style={{width: "78%", float:"left", height:"70vh", padding:"20px"}}>
        <Map/>
      </div>
    </div>
    <Info></Info>
    <EndBar></EndBar>
    
  </div>

  )
}
