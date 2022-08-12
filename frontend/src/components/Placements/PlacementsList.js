import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";

function PlacementsList(){

    const [placements, error, loading, axiosFetch] = useAxios();

    const columns = {
        name: "Name",
        numberOfClasses: "Number of classes",
        createdTime: "Created Time",
        numberOfResults: "Results"
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/placements',
        });
    };

    useEffect(() => {
        getData();
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && placements && <TableList linkTo={{field: 'name', basePath: '/placements/'}} items={extractListFromAPI(placements, 'placementList')} columns={columns} />}
        </>
    );
}

export default PlacementsList;