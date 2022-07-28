import React from 'react';
import { NavLink } from "react-router-dom";

function Menu() {
    return (
        <nav>
            <ul>
                <li><NavLink to="/">Home</NavLink></li>
                <li><NavLink to="/pupils">Pupils</NavLink></li>
                <li><NavLink to="/groups">Groups</NavLink></li>
                <li><NavLink to="/templates">Templates</NavLink></li>
                <li><NavLink to="/attributes">Attributes</NavLink></li>
                <li><NavLink to="/placements">Placements</NavLink></li>
            </ul>
        </nav>
    );
}

export default Menu;