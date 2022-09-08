import React from 'react';
import HeroAddRecord from "../General/HeroAddRecord";
import {Button} from "react-bootstrap";
import {LinkContainer} from 'react-router-bootstrap';

export default function HomePage() {
    return (
        <HeroAddRecord
            title={<h1>Welcome to PlaceMe!</h1>}
            message={<p>Go to placements page to start</p>}
            button={<LinkContainer to="/placements"><Button>Start Now</Button></LinkContainer>}
        />
    )
}