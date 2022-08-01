import React from 'react';
import { Button } from "react-bootstrap";
import { LinkContainer } from 'react-router-bootstrap'
import {Outlet} from "react-router-dom";

export default function Pupils() {
    return (
        <main>
            <h1>Pupils</h1>
            <LinkContainer to="add"><Button>Add Pupil</Button></LinkContainer>
            <Outlet />
        </main>
    );
}
