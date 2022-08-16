import React, {useMemo, useState} from 'react';
import {Col, Nav, Row, Tab} from "react-bootstrap";
import Attributes from "./Attributes";
import {extractListFromAPI} from "../../utils";
import {useParams} from "react-router-dom";

export default function GroupsAttributes({ pupilGroups }) {
    const { pupilId } = useParams();
    const groups = useMemo(() => extractListFromAPI(pupilGroups, 'groupList'), [pupilGroups]);
    const [group, setGroup] = useState( groups && groups.length > 0 ? groups[0] : null );

    const onSelect = eventKey => {
        setGroup(groups.find(g => g.id.toString() === eventKey))
    };

    return (
        <>
            <h3>Attribute Values</h3>
            <Tab.Container id="pupil-attributes" defaultActiveKey={group?.id} onSelect={onSelect}>
                <Row>
                    <Col sm={3}>
                        <Nav variant="pills" className="flex-column">
                            {groups.map( group => (
                                <Nav.Item key={group.id}>
                                    <Nav.Link eventKey={group.id} href="#" onClick={e => e.preventDefault()}>
                                        {group.name}
                                    </Nav.Link>
                                </Nav.Item>
                            ))}
                        </Nav>
                    </Col>
                    <Col sm={9}>
                        <Tab.Content>
                            {group && (
                                <Tab.Pane key={group.id} eventKey={group.id}>
                                    <Attributes group={group} pupilId={pupilId} />
                                </Tab.Pane>
                            )}
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </>
    )
}