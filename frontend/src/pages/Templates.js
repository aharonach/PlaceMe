import React from 'react';
import {Outlet} from "react-router-dom";
import {LinkContainer} from "react-router-bootstrap";
import {Button} from "react-bootstrap";

export default function Templates() {
    return (
        <main>
            <h1>Templates</h1>
            <LinkContainer to="add"><Button>Add Template</Button></LinkContainer>
            <Outlet />
        </main>
    );
}
