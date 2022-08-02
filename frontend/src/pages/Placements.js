import React from 'react';
import {Outlet} from "react-router-dom";
import {LinkContainer} from "react-router-bootstrap";
import {Button} from "react-bootstrap";

export default function Placements() {
    return (
        <main>
            <h1>Placements</h1>
            <LinkContainer to="add"><Button>Add Placement</Button></LinkContainer>
            <Outlet />
        </main>
    );
}
