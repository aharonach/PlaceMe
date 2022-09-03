import {Button} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";

export default function HeroAddRecord({ title, message, button }) {
    return (
        <div className="p-5 mb-4 bg-light rounded-3 text-center">
            <div className="container-fluid py-5">
                {title ? title : <h2>No records yet.</h2>}
                {message ? message : <p>Click on create to start!</p>}
                {button ? button : <LinkContainer to="add"><Button size="lg">Create New</Button></LinkContainer>}
            </div>
        </div>
    )
}