import React from 'react';
import {Link} from "react-router-dom";
import {Alert, Button, Table} from "react-bootstrap";
import {CaretDownFill, CaretUpFill} from "react-bootstrap-icons";
import {humanizeTime} from "../utils";

function TableList({
    items,
    columns,
    linkTo = {field: 'id', basePath: ''},
    numbering= {enabled: true, startFrom: 1},
    bsProps = {bordered: true, hover: true},
    nothingToShow = false,
    sorting,
    direction
}) {

    if ( nothingToShow ) {
        if ( ! items || items.length <= 0 ) {
            return (
                <Alert variant="info">Nothing to show.</Alert>
            )
        }
    }

    let currentNumber = numbering.startFrom ? Number(numbering.startFrom) : 1;

    return (
        <div className="table-responsive">
            <Table {...bsProps} style={{ tableLayout: "fixed" }}>
                <thead className="table-light">
                <tr>
                    {numbering.enabled && <th style={{ width: "50px" }}>#</th>}

                    {columns && Object.keys(columns).map( key => {
                        if ( 'actions' === key ) {
                            return <th scope="col" key={key}>{columns[key].label}</th>;
                        }

                        let label = columns[key];

                        if ( sorting && sorting.fields.includes( key ) ) {
                            const activeSort = sorting.value === key;
                            const directionArrow = <span className="ms-1">
                                {direction.value === 'ASC' ? <CaretUpFill /> : <CaretDownFill />}
                            </span>;
                            const changeSort = () => {
                                sorting.set(key);
                                direction.set(direction.value === 'ASC' ? 'DESC' : 'ASC');
                            };

                            label = <>
                                <Button
                                    variant="link"
                                    className="p-0"
                                    active={activeSort}
                                    onClick={changeSort}
                                >{label}</Button>
                                {activeSort && directionArrow}
                            </>;
                        }

                        return <th scope="col" key={key}>{label}</th>;
                    })}
                </tr>
                </thead>
                <tbody>
                {items && items.map( item => (
                    <tr key={item['id']}>
                        {numbering.enabled && <th scope="row">{currentNumber++}</th>}

                        {Object.keys(columns).map( key => {
                            // return empty cell for the moment.
                            if ( linkTo.field === key ) {
                                return <td key={key}><Link to={linkTo.basePath + item['id']}>{item[key]}</Link></td>
                            }

                            if ( 'actions' === key ) {
                                return <td key={key}>{columns[key].callbacks.map( callback => callback(item))}</td>
                            }

                            if ( 'createdTime' === key ) {
                                return <td key={key}>{humanizeTime(item[key])}</td>;
                            }

                            if ( ['undefined', 'object'].includes( typeof( item[key] ) ) && ! React.isValidElement( item[key] ) ) {
                                return <td key={key}></td>;
                            }

                            return <td key={key}>{item[key]}</td>
                        })}
                    </tr>
                ))}
                </tbody>
            </Table>
        </div>
    );
}

export default TableList;