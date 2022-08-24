import React, {useEffect} from 'react';
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import {Badge, Card, Row} from "react-bootstrap";
import {extractListFromAPI} from "../../utils";
import * as PropTypes from "prop-types";


function Strong(props) {
    return null;
}

Strong.propTypes = {children: PropTypes.node};
export default function PlacementResultData({placementId, placementResult}){

    const [response, error, loading, axiosFetch] = useAxios();
    let classNumber = 1;

    const getClasses = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/placements/${placementId}/results/${placementResult.id}/classes`,
        });
    }

    useEffect(() => {
        getClasses();
    }, []);

    return (
        <>
            <h3>Result:</h3>
            <div style={{display: 'flex', flexDirection: 'row', flexWrap: 'wrap'}}>
                { response && extractListFromAPI(response, "placementClassroomList").map(classInfo => (
                    <Card style={{ width: '18rem', marginRight:10 }} className="mb-2">
                        <Card.Header as={"h3"}>Class #{classNumber++}</Card.Header>
                        <Card.Body>
                            <Card.Title>Info</Card.Title>
                            <Card.Text>
                                <div><strong>Pupils:</strong> {classInfo.numOfPupils}</div>
                                <div><strong>Score:</strong> {classInfo.classScore}</div>
                                <div><strong>Males:</strong> {classInfo.numberOfMales} | <strong>Females:</strong> {classInfo.numberOfFemales}</div>
                            </Card.Text>
                            <Card.Title>Pupils</Card.Title>
                            {classInfo.pupils && classInfo.pupils.map(pupil => (
                                <div>{pupil.firstName} {pupil.lastName} <Badge bg={pupil.gender === "MALE" ? 'success ' : 'secondary  '}>{pupil.gender}</Badge></div>
                            ))}
                        </Card.Body>
                    </Card>
                )) }
            </div>
        </>
    );
}