import React from 'react';
import {Container, Nav, Navbar} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";

export default function Menu() {
    return (
        <Navbar>
            <Container>
                <LinkContainer to="/"><Navbar.Brand>PlaceMe</Navbar.Brand></LinkContainer>
                <Navbar.Collapse>
                    <Nav className="me-auto">
                        <LinkContainer to="/pupils"><Nav.Link href="#home">Pupils</Nav.Link></LinkContainer>
                        <LinkContainer to="/groups"><Nav.Link href="#home">Groups</Nav.Link></LinkContainer>
                        <LinkContainer to="/templates"><Nav.Link href="#home">Templates</Nav.Link></LinkContainer>
                        <LinkContainer to="/placements"><Nav.Link href="#home">Placements</Nav.Link></LinkContainer>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
